package com.example.dgtechhealthcare.editProfile

import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class EditProfileModel(val view: View) {

    val reference = FirebasePresenter(view)

    fun editPatientInfo(patientDetails : PatientEditData){

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("username").value.toString()
                val mobile = snapshot.child("contactNo").value.toString()
                val dateOBirth = snapshot.child("dateOfBirth").value.toString()

                patientDetails.name.setText(name)
                patientDetails.mob.setText(mobile)
                patientDetails.dob.setText(dateOBirth)
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    fun updatePatientProfile(patientDetails: PatientEditData) {
        reference.userReference.child(reference.currentUserId!!).child("username").setValue(patientDetails.name.text.toString())
        reference.userReference.child(reference.currentUserId!!).child("contactNo").setValue(patientDetails.mob.text.toString())
        reference.userReference.child(reference.currentUserId!!).child("dateOfBirth").setValue(patientDetails.dob.text.toString())

        val genderID = patientDetails.gender.checkedRadioButtonId
        val rB : RadioButton = view.findViewById(genderID)

        reference.userReference.child(reference.currentUserId!!).child("gender").setValue(rB.text.toString())
        reference.userReference.child(reference.currentUserId!!).child("fatherName").setValue(patientDetails.father.text.toString())
        reference.userReference.child(reference.currentUserId!!).child("motherName").setValue(patientDetails.mother.text.toString())
        reference.userReference.child(reference.currentUserId!!).child("otherDetes").setValue(patientDetails.details.text.toString())

        if (patientDetails.doctor.text.toString().isNotEmpty() && patientDetails.hospital.text.toString().isNotEmpty()) {
            reference.userReference.child(reference.currentUserId!!).child("doctorName").setValue(patientDetails.doctor.text.toString())
            reference.userReference.child(reference.currentUserId!!).child("hostpitalName").setValue(patientDetails.hospital.text.toString())

            reference.doctorReference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(data in snapshot.children){
                        if(data.child("username").value.toString()
                                .compareTo(patientDetails.doctor.text.toString()) == 0 &&
                            data.child("hospital").value.toString()
                                .compareTo(patientDetails.hospital.text.toString()) == 0) {
                                    val hm = HashMap<String,Any>()
                                    hm["puid"] = reference.currentUserId.toString()
                                    reference.doctorReference.child(data.child("duid").value.toString())
                                        .child("patients").child(reference.currentUserId).updateChildren(hm).addOnCompleteListener {
                                            if(it.isSuccessful) {
                                                Toast.makeText(view.context,"Doctor registered",Toast.LENGTH_SHORT)
                                            }
                                        }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }


    }

}