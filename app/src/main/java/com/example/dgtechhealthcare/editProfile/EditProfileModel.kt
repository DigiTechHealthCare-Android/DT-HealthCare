package com.example.dgtechhealthcare.editProfile

import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.nurse.model.NurseData
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_edit_content_manager_profile.*

class EditProfileModel(val view: View) {

    val reference = FirebasePresenter(view)

    fun editPatientInfo(patientDetails : PatientClass){

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("username").value.toString()
                val mobile = snapshot.child("contactNo").value.toString()
                val dateOBirth = snapshot.child("dateOfBirth").value.toString()
                val gender = snapshot.child("gender").value.toString()
                val father = snapshot.child("fatherName").value.toString()
                val mother = snapshot.child("motherName").value.toString()
                val other = snapshot.child("otherDetes").value.toString()
                val doctor = snapshot.child("doctorName").value.toString()
                val hospital = snapshot.child("hostpitalName").value.toString()

                patientDetails.name.setText(name)
                patientDetails.mob.setText(mobile)
                patientDetails.dob.setText(dateOBirth)
                if(gender.compareTo("Male")==0){
                    patientDetails.gender.check(R.id.editPMale)
                } else if(gender.compareTo("Female")==0) patientDetails.gender.check(R.id.editPFemale)
                else if(gender.compareTo("Other")==0) patientDetails.gender.check(R.id.editPOther)

                if (father.isNullOrEmpty() || !snapshot.hasChild("fatherName")) patientDetails.father.setText("")
                else patientDetails.father.setText(father)

                if (mother.isNullOrEmpty() || !snapshot.hasChild("motherName")) patientDetails.mother.setText("")
                else patientDetails.mother.setText(mother)

                if (other.isNullOrEmpty() || !snapshot.hasChild("otherDetes")) patientDetails.details.setText("")
                else patientDetails.details.setText(other)

                if (doctor.isNullOrEmpty() || !snapshot.hasChild("doctorName")) patientDetails.doctor.setText("")
                else patientDetails.doctor.setText(doctor)

                if (hospital.isNullOrEmpty() || !snapshot.hasChild("hostpitalName")) patientDetails.hospital.setText("")
                else patientDetails.hospital.setText(hospital)
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    fun updatePatientProfile(patientDetails: PatientClass) {
        reference.userReference.child(reference.currentUserId!!).child("username").setValue(patientDetails.name.text.toString())
        reference.userReference.child(reference.currentUserId!!).child("contactNo").setValue(patientDetails.mob.text.toString())
        reference.userReference.child(reference.currentUserId!!).child("dateOfBirth").setValue(patientDetails.dob.text.toString())

        val genderID = patientDetails.gender.checkedRadioButtonId
        val rB : RadioButton = view.findViewById(genderID)

        reference.userReference.child(reference.currentUserId!!).child("gender").setValue(rB.text.toString())
        if (patientDetails.father.text.toString().isNotEmpty()){
            reference.userReference.child(reference.currentUserId!!).child("fatherName").setValue(patientDetails.father.text.toString())
        }
        else{
            reference.userReference.child(reference.currentUserId!!).child("fatherName").setValue("-")
        }
        if (patientDetails.mother.text.toString().isNotEmpty()){
            reference.userReference.child(reference.currentUserId!!).child("motherName").setValue(patientDetails.mother.text.toString())
        }
        else{
            reference.userReference.child(reference.currentUserId!!).child("motherName").setValue("-")
        }
        if (patientDetails.details.text.toString().isNotEmpty()){
            reference.userReference.child(reference.currentUserId!!).child("otherDetes").setValue(patientDetails.details.text.toString())
        }
        else{
            reference.userReference.child(reference.currentUserId!!).child("otherDetes").setValue("-")
        }

        if (patientDetails.doctor.text.toString().isNotEmpty() && patientDetails.hospital.text.toString().isNotEmpty()) {
            if (patientDetails.doctor.text.toString().isNotEmpty()){
                reference.userReference.child(reference.currentUserId!!).child("doctorName").setValue(patientDetails.doctor.text.toString())
            }
            else{
                reference.userReference.child(reference.currentUserId!!).child("doctorName").setValue("-")
            }
            if (patientDetails.hospital.text.toString().isNotEmpty()){
                reference.userReference.child(reference.currentUserId!!).child("hostpitalName").setValue(patientDetails.hospital.text.toString())
            }
            else{
                reference.userReference.child(reference.currentUserId!!).child("hostpitalName").setValue("-")
            }


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
                                                EditPatientProfileFragment().doctorUpdated(view.context)
                                            }
                                        }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        // PatientData in Nurse Node in Firebase

        if (patientDetails.name.text.isNotEmpty() && patientDetails.hospital.text.isNotEmpty()){

            reference.nurseReference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snp in snapshot.children){
                        val nurseClass = snp.getValue(NurseData::class.java)

                        if (nurseClass?.hospitalName?.equals(patientDetails.hospital.text.toString()) == true){
                            val pData = HashMap<String, Any>()
                            pData["puid"] = reference.currentUserId.toString()
                            pData["pName"] = patientDetails.name.text.toString()

                            reference.nurseReference.child(snp.child("nuid").value.toString()).child("patients").child(reference.currentUserId).updateChildren(pData)

                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
        EditPatientProfileFragment().profileUpdated(view.context)
    }

    fun editDoctorInfo(doctorDetails: DoctorClass){
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("username").value.toString()
                val mob = snapshot.child("contact").value.toString()
                val hospital = snapshot.child("hospital").value.toString()
                val special = snapshot.child("specialization").value.toString()

                doctorDetails.name.setText(name)
                doctorDetails.contact.setText(mob)
                doctorDetails.hospital.setText(hospital)
                doctorDetails.specialization.setText(special)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun updateDoctorProfile(doctorDetails: DoctorClass){
        val hm = HashMap<String,Any>()
        hm["username"] = doctorDetails.name.text.toString()
        hm["contact"] = doctorDetails.contact.text.toString()
        hm["hospital"] = doctorDetails.hospital.text.toString()
        hm["specialization"] = doctorDetails.specialization.text.toString()

        reference.userReference.child(reference.currentUserId!!).updateChildren(hm).addOnCompleteListener {
            if(it.isSuccessful) EditDoctorProfileFragment().profileUpdated(view.context)
        }
    }

    fun editContentManagerInfo(managerDetails: ManagerClass) {
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                managerDetails.name.setText(snapshot.child("username").value.toString())
                managerDetails.location.setText(snapshot.child("location").value.toString())
                managerDetails.email.setText(snapshot.child("email").value.toString())
                managerDetails.contact.setText(snapshot.child("contact").value.toString())
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun updateManagerProfile(managerDetails: ManagerClass) {
        val hm = HashMap<String,Any>()
        hm["username"] = managerDetails.name.text.toString()
        hm["contact"] = managerDetails.contact.text.toString()
        hm["location"] = managerDetails.location.text.toString()
        hm["email"] = managerDetails.email.text.toString()
        reference.userReference.child(reference.currentUserId!!).updateChildren(hm).addOnCompleteListener {
            if(it.isSuccessful){
                EditContentManagerProfileFragment().profileUpdated(view.context)
            }
        }
    }
}