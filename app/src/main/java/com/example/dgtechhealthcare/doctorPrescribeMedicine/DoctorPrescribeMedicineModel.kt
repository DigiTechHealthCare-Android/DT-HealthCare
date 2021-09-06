package com.example.dgtechhealthcare.doctorPrescribeMedicine

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.pushNotification.FirebaseNotificationService
import com.example.dgtechhealthcare.pushNotification.NotificationData
import com.example.dgtechhealthcare.pushNotification.PushNotification
import com.example.dgtechhealthcare.pushNotification.RetrofitInstance
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class DoctorPrescribeMedicineModel(view: View) {

    private val TAG = "DoctorPrescribe"

    val reference = FirebasePresenter(view)

    fun populateModelView(data: PrescrideMedicine, userType: String?) {
        reference.userReference.child(userType!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("prescribedMedicine").child("morning").hasChild("name")){
                    data.morningMed.setText(snapshot.child("prescribedMedicine").child("morning").child("name").value.toString())
                    data.morningCheckBox.isChecked =
                        snapshot.child("prescribedMedicine").child("morning").child("status").value.toString() == "medicine given"
                } else data.morningMed.setText("")
                if(snapshot.child("prescribedMedicine").child("afternoon").hasChild("name")){
                    data.afternoonMed.setText(snapshot.child("prescribedMedicine").child("afternoon").child("name").value.toString())
                    data.afternoonCheckBox.isChecked =
                        snapshot.child("prescribedMedicine").child("afternoon").child("status").value.toString() == "medicine given"
                } else data.afternoonMed.setText("")
                if(snapshot.child("prescribedMedicine").child("evening").hasChild("name")){
                    data.eveningMed.setText(snapshot.child("prescribedMedicine").child("evening").child("name").value.toString())
                    if (snapshot.child("prescribedMedicine").child("evening").child("status").value.toString() == "medicine given"){
                        data.eveningCheckBox.isChecked = true
                    }
                } else data.eveningMed.setText("")
                if(snapshot.child("prescribedMedicine").child("night").hasChild("name")){
                    data.nightMed.setText(snapshot.child("prescribedMedicine").child("night").child("name").value.toString())
                    if (snapshot.child("prescribedMedicine").child("night").child("status").value.toString() == "medicine given"){
                        data.nightCheckBox.isChecked = true
                    }
                } else data.nightMed.setText("")
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendRequestModel(choice: String, requireActivity: FragmentActivity) {
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("prescribedMedicine")){
                    val med1 = snapshot.child("prescribedMedicine").child("morning").child("name").value.toString()
                    val med2 = snapshot.child("prescribedMedicine").child("afternoon").child("name").value.toString()
                    val med3 = snapshot.child("prescribedMedicine").child("evening").child("name").value.toString()
                    val med4 = snapshot.child("prescribedMedicine").child("night").child("name").value.toString()

                    val hashMap = HashMap<String,Any>()
                    hashMap["puid"] = reference.currentUserId!!
                    hashMap["med1"] = med1
                    hashMap["med2"] = med2
                    hashMap["med3"] = med3
                    hashMap["med4"] = med4

                    val username = snapshot.child("username").value.toString()

                    reference.pharmaReference.child(choice).child("requests").child(reference.currentUserId!!).updateChildren(hashMap).addOnCompleteListener {
                        DoctorPrescribeMedicinePresenter(View(requireActivity)).requestSentMessage(requireActivity)
                        //getToken("New Request")
                        reference.userReference.child(choice).addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val token = snapshot.child("token").value.toString()
                                FirebaseNotificationService.sharedPref = requireActivity?.getSharedPreferences("sharedPref",
                                    Context.MODE_PRIVATE)
                                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                val title = "New Order"
                                val message = "Request from $username"
                                PushNotification(
                                    NotificationData(title,message,reference.currentUserId!!)
                                    , token).also {
                                    sendNotification(it)
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun prescribeMedicineModel(patientID: String?, userType: String?, requireActivity: FragmentActivity, data: PrescrideMedicine)
    {
        val ref = reference.userReference.child(patientID!!).child("prescribedMedicine")
        ref.child("morning").child("name").setValue(data.morningMed.text.toString())
        ref.child("afternoon").child("name").setValue(data.afternoonMed.text.toString())
        ref.child("evening").child("name").setValue(data.eveningMed.text.toString())
        ref.child("night").child("name").setValue(data.nightMed.text.toString())

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").value.toString()

                reference.userReference.child(userType!!).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val token = snapshot.child("token").value.toString()
                        FirebaseNotificationService.sharedPref = requireActivity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
                        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                        val title = "New Prescription"
                        val message = "From Dr. $username"
                        PushNotification(NotificationData(title,message,"doctor"), token)
                            .also {
                            sendNotification(it)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun nurseStatusModel(patientID: String?, data: PrescrideMedicine,activity:FragmentActivity) {
        val ref = reference.userReference.child(patientID!!).child("prescribedMedicine")

        var countCheckedBox = 0

        if (data.morningCheckBox.isChecked) countCheckedBox += 1 else countCheckedBox -= 1
        if (countCheckedBox >= 1){
            val mref = ref.child("morning").child("status")
            if (data.morningCheckBox.isChecked) mref.setValue("medicine given")
            else mref.setValue("medicine not given")

            val aref = ref.child("afternoon").child("status")
            if (data.afternoonCheckBox.isChecked) aref.setValue("medicine given")
            else aref.setValue("medicine not given")

            val eref = ref.child("evening").child("status")
            if (data.eveningCheckBox.isChecked) eref.setValue("medicine given")
            else eref.setValue("medicine not given")

            val nref = ref.child("night").child("status")
            if (data.nightCheckBox.isChecked) nref.setValue("medicine given")
            else nref.setValue("medicine not given")

            DoctorPrescribeMedicinePresenter(View(activity)).statusUpdateMessage(activity)
        }
        else{
            DoctorPrescribeMedicinePresenter(View(activity)).checkBoxMessage(activity)
        }
    }


    private fun sendNotification(it: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(it)
            if (response.isSuccessful){
                Log.d(TAG,"Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG,response.errorBody().toString())
            }
        } catch (e : Exception){
            Log.e(TAG, e.toString())
        }
    }

    fun checkAccountModel(userType: String?, data: PrescrideMedicine) {
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            var accountType = ""
            override fun onDataChange(snapshot: DataSnapshot) {
                accountType = snapshot.child("accountType").value.toString()
                if(accountType.compareTo("patient")==0 && userType!!.compareTo(reference.currentUserId!!)==0){
                    data.prescribeB.text = "Request prescription"
                    data.morningMed.isEnabled = false
                    data.afternoonMed.isEnabled = false
                    data.eveningMed.isEnabled = false
                    data.nightMed.isEnabled = false
                    data.morningCheckBox.visibility = View.INVISIBLE
                    data.afternoonCheckBox.visibility = View.INVISIBLE
                    data.eveningCheckBox.visibility = View.INVISIBLE
                    data.nightCheckBox.visibility = View.INVISIBLE
                }
                else if(accountType.compareTo("doctor")==0 && userType!!.compareTo(reference.currentUserId!!)!=0){
                    data.prescribeB.text = "Prescribe Medicine"
                    data.morningCheckBox.visibility = View.INVISIBLE
                    data.afternoonCheckBox.visibility = View.INVISIBLE
                    data.eveningCheckBox.visibility = View.INVISIBLE
                    data.nightCheckBox.visibility = View.INVISIBLE

                }
                else if (accountType.compareTo("nurse")==0 && userType!!.compareTo(reference.currentUserId!!)!=0){
                    data.prescribeB.text = "Update Status"
                    data.morningMed.isEnabled = false
                    data.afternoonMed.isEnabled = false
                    data.eveningMed.isEnabled = false
                    data.nightMed.isEnabled = false
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


}