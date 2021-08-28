package com.example.dgtechhealthcare.doctorPrescribeMedicine

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.pushNotification.*
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

const val TOPIC = "/topics/myTopic2"

class DoctorPrescribeMedicineFragment : Fragment(), AdapterView.OnItemSelectedListener {

    val TAG = "DoctorPrescribe"

    lateinit var morningMed : EditText
    lateinit var afternoonMed : EditText
    lateinit var eveningMed : EditText
    lateinit var nightMed : EditText
    lateinit var prescribeB : Button
    lateinit var morningCheckBox: CheckBox
    lateinit var afternoonCheckBox: CheckBox
    lateinit var eveningCheckBox: CheckBox
    lateinit var nightCheckBox: CheckBox

    lateinit var reference : FirebasePresenter

    var patientID :String? = ""
    var userType : String? = ""

    val nameList = arrayListOf<String>()
    var choice = ""
    var fromDoctor = ""
    val newHM = HashMap<String,String>()

    lateinit var pharmaChoice : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_prescribe_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)
        patientID = arguments?.getString("patientID","")

        initializeValues(view)

        if(patientID.isNullOrEmpty()){
            userType = reference.currentUserId
        } else {
            userType = patientID
        }

        reference.userReference.child(userType!!).addValueEventListener(object : ValueEventListener{
            var accountType = ""
            override fun onDataChange(snapshot: DataSnapshot) {
                accountType = snapshot.child("accountType").value.toString()
                if(accountType.compareTo("patient")==0 && userType!!.compareTo(reference.currentUserId!!)==0){
                    prescribeB.text = "Request prescription"
                    morningMed.isEnabled = false
                    afternoonMed.isEnabled = false
                    eveningMed.isEnabled = false
                    nightMed.isEnabled = false
                    morningCheckBox.isEnabled = false
                    afternoonCheckBox.isEnabled = false
                    eveningCheckBox.isEnabled = false
                    nightCheckBox.isEnabled = false
                }
                else if(accountType.compareTo("doctor")==0 || userType!!.compareTo(reference.currentUserId!!)!=0){
                    prescribeB.text = "Prescribe Medicine"
//                    morningCheckBox.isEnabled = false
//                    afternoonCheckBox.isEnabled = false
//                    eveningCheckBox.isEnabled = false
//                    nightCheckBox.isEnabled = false
                }
//                else if (accountType.compareTo("nurse")==0 || userType!!.compareTo(reference.currentUserId!!)!=0){
//                    prescribeB.setText("Update Status")
//                    morningMed.isEnabled = false
//                    afternoonMed.isEnabled = false
//                    eveningMed.isEnabled = false
//                    nightMed.isEnabled = false
//                }
                if(snapshot.child("prescribedMedicine").hasChild("morning")){
                    morningMed.setText(snapshot.child("prescribedMedicine").child("morning").child("name").value.toString())
                    morningCheckBox.isChecked =
                        snapshot.child("prescribedMedicine").child("morning").child("status").value.toString() == "medicine given"
                }
                if(snapshot.child("prescribedMedicine").hasChild("afternoon")){
                    afternoonMed.setText(snapshot.child("prescribedMedicine").child("afternoon").child("name").value.toString())
                    afternoonCheckBox.isChecked =
                        snapshot.child("prescribedMedicine").child("afternoon").child("status").value.toString() == "medicine given"
                }
                if(snapshot.child("prescribedMedicine").hasChild("evening")){
                    eveningMed.setText(snapshot.child("prescribedMedicine").child("evening").child("name").value.toString())
                    if (snapshot.child("prescribedMedicine").child("evening").child("status").value.toString() == "medicine given"){
                        eveningCheckBox.isChecked = true
                    }
                }
                if(snapshot.child("prescribedMedicine").hasChild("night")){
                    nightMed.setText(snapshot.child("prescribedMedicine").child("night").child("name").value.toString())
                    if (snapshot.child("prescribedMedicine").child("night").child("status").value.toString() == "medicine given"){
                        nightCheckBox.isChecked = true
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })

        val builder = AlertDialog.Builder(activity)
        val dialogLayout = layoutInflater.inflate(R.layout.choose_pharmacy_layout,null)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val pharmaSpinner = dialogLayout.findViewById<Spinner>(R.id.pharmaNameSpinner)
        pharmaChoice = dialogLayout.findViewById<TextView>(R.id.pharmaNameChoiceTV)
        prescribeB.setOnClickListener {
            if(prescribeB.text.toString().compareTo("Request prescription")==0){
                reference.pharmaReference.child("pharmacyNames").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children){
                            nameList.add(data.key.toString())
                            newHM.put(data.key.toString(),data.value.toString())
                        }
                        val adapter = ArrayAdapter<String>(it.context,R.layout.support_simple_spinner_dropdown_item,nameList)
                        pharmaSpinner.adapter = adapter
                        pharmaSpinner.onItemSelectedListener = this@DoctorPrescribeMedicineFragment
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })

                with(builder){
                    setTitle("Choose your preferred pharmacy")
                    setPositiveButton("Request"){dialog,which ->
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
                                        Toast.makeText(context,"Request Sent",Toast.LENGTH_SHORT).show()
                                        //getToken("New Request")
                                        reference.userReference.child(choice).addValueEventListener(object : ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val token = snapshot.child("token").value.toString()
                                                FirebaseNotificationService.sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
                                                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                                val title = "New Order"
                                                val message = "Request from $username"
                                                PushNotification(NotificationData(title,message,)
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
                    requireActivity()?.supportFragmentManager?.popBackStack()
                    }
                    setNegativeButton("Cancel"){dialog,which->}
                }
                builder.show()
            }else if(prescribeB.text.toString().compareTo("Prescribe Medicine")==0) {
                val ref = reference.userReference.child(patientID!!).child("prescribedMedicine")
                ref.child("morning").child("name").setValue(morningMed.text.toString())
                val mref = ref.child("morning").child("status")
                if (morningCheckBox.isChecked){
                    mref.setValue("medicine given") }
                else{
                    mref.setValue("medicine not given") }

                ref.child("afternoon").child("name").setValue(afternoonMed.text.toString())
                val aref = ref.child("afternoon").child("status")
                if (afternoonCheckBox.isChecked){
                    aref.setValue("medicine given")
                }
                else{
                    aref.setValue("medicine not given")
                }

                ref.child("evening").child("name").setValue(eveningMed.text.toString())
                val eref = ref.child("evening").child("status")
                if (eveningCheckBox.isChecked){
                    eref.setValue("medicine given")
                }
                else{
                    eref.setValue("medicine not given")
                }

                ref.child("night").child("name").setValue(nightMed.text.toString())
                val nref = ref.child("night").child("status")
                if (nightCheckBox.isChecked){
                    nref.setValue("medicine given")
                }
                else{
                    nref.setValue("medicine not given")
                }

                reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.child("username").value.toString()

                        reference.userReference.child(userType!!).addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val token = snapshot.child("token").value.toString()
                                FirebaseNotificationService.sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
                                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                val title = "New Prescription"
                                val message = "From Dr. $username"
                                PushNotification(NotificationData(title,message,"doctor")
                                    , token
                                ).also {
                                    sendNotification(it)
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
                Toast.makeText(activity,"Prescription given",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        if (view != null) {
            val parentViewGroup = requireView().parent as ViewGroup?
            parentViewGroup?.removeAllViews();
        }
        super.onDestroyView()
    }

    private fun initializeValues(view: View) {
        morningMed = view.findViewById(R.id.morningMed)
        afternoonMed = view.findViewById(R.id.afternoonMed)
        eveningMed = view.findViewById(R.id.eveningMed)
        nightMed = view.findViewById(R.id.nightMed)
        prescribeB = view.findViewById(R.id.addMedicineB)
        morningCheckBox = view.findViewById(R.id.morningCheckBox)
        afternoonCheckBox = view.findViewById(R.id.afternoonCheckBox)
        eveningCheckBox = view.findViewById(R.id.eveningCheckBox)
        nightCheckBox = view.findViewById(R.id.nightCheckBox)
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val name = parent?.selectedItem
        for(h in newHM){
            if(h.key.toString().compareTo(name.toString())==0){
                choice = h.value.toString()
            }
        }
        pharmaChoice.setText(name.toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}