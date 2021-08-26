package com.example.dgtechhealthcare.doctorPrescribeMedicine

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoctorPrescribeMedicineFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var morningMed : EditText
    lateinit var afternoonMed : EditText
    lateinit var eveningMed : EditText
    lateinit var nightMed : EditText
    lateinit var prescribeB : Button

    lateinit var reference : FirebasePresenter

    var patientID :String? = ""
    var userType : String? = ""

    val nameList = arrayListOf<String>()
    var choice = ""
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
                    prescribeB.setText("Request prescription")
                    morningMed.isEnabled = false
                    afternoonMed.isEnabled = false
                    eveningMed.isEnabled = false
                    nightMed.isEnabled = false
                } else if(accountType.compareTo("doctor")==0 || userType!!.compareTo(reference.currentUserId!!)!=0){
                    prescribeB.setText("Prescribe Medicine")
                }
                if(snapshot.child("prescribedMedicine").hasChild("morning")){
                    morningMed.setText(snapshot.child("prescribedMedicine").child("morning").child("name").value.toString())
                }
                if(snapshot.child("prescribedMedicine").hasChild("afternoon")){
                    afternoonMed.setText(snapshot.child("prescribedMedicine").child("afternoon").child("name").value.toString())
                }
                if(snapshot.child("prescribedMedicine").hasChild("evening")){
                    eveningMed.setText(snapshot.child("prescribedMedicine").child("evening").child("name").value.toString())
                }
                if(snapshot.child("prescribedMedicine").hasChild("night")){
                    nightMed.setText(snapshot.child("prescribedMedicine").child("night").child("name").value.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })

        val builder = AlertDialog.Builder(activity)
        val dialogLayout = layoutInflater.inflate(R.layout.choose_pharmacy_layout,null)
        builder.setView(dialogLayout)

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

                                    reference.pharmaReference.child(choice).child("requests").child(reference.currentUserId!!).updateChildren(hashMap).addOnCompleteListener {
                                        Toast.makeText(activity,"Request Sent",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                    setNegativeButton("Cancel"){dialog,which->}
                }
                builder.show()
            }else if(prescribeB.text.toString().compareTo("Prescribe Medicine")==0) {
                val ref = reference.userReference.child(patientID!!).child("prescribedMedicine")
                ref.child("morning").child("name").setValue(morningMed.text.toString())
                ref.child("afternoon").child("name").setValue(afternoonMed.text.toString())
                ref.child("evening").child("name").setValue(eveningMed.text.toString())
                ref.child("night").child("name").setValue(nightMed.text.toString())
                Toast.makeText(activity,"Prescription given",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initializeValues(view: View) {
        morningMed = view.findViewById(R.id.morningMed)
        afternoonMed = view.findViewById(R.id.afternoonMed)
        eveningMed = view.findViewById(R.id.eveningMed)
        nightMed = view.findViewById(R.id.nightMed)
        prescribeB = view.findViewById(R.id.addMedicineB)
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