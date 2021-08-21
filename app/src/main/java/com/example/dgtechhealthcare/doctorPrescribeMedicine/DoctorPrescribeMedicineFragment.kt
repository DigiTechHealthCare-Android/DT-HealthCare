package com.example.dgtechhealthcare.doctorPrescribeMedicine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoctorPrescribeMedicineFragment : Fragment() {

    lateinit var morningMed : EditText
    lateinit var afternoonMed : EditText
    lateinit var eveningMed : EditText
    lateinit var nightMed : EditText
    lateinit var prescribeB : Button

    lateinit var reference : FirebasePresenter

    var patientID :String? = ""

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

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            var accountType = ""
            override fun onDataChange(snapshot: DataSnapshot) {
                accountType = snapshot.child("accountType").value.toString()
                if(accountType.compareTo("patient")==0){
                    prescribeB.setText("Request prescription")
                    morningMed.isEnabled = false
                    afternoonMed.isEnabled = false
                    eveningMed.isEnabled = false
                    nightMed.isEnabled = false
                } else if(accountType.compareTo("doctor")==0){
                    prescribeB.setText("Prescribe Medicine")
                }
                morningMed.setText(snapshot.child("prescribedMedicine").child("morning").child("name").value.toString())
                afternoonMed.setText(snapshot.child("prescribedMedicine").child("afternoon").child("name").value.toString())
                eveningMed.setText(snapshot.child("prescribedMedicine").child("evening").child("name").value.toString())
                nightMed.setText(snapshot.child("prescribedMedicine").child("night").child("name").value.toString())
            }
            override fun onCancelled(error: DatabaseError) {}

        })

        prescribeB.setOnClickListener {
            if(prescribeB.text.toString().compareTo("Request prescription")==0){
                Toast.makeText(activity,"Request Sent",Toast.LENGTH_SHORT).show()
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
}