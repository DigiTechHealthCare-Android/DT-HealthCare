package com.example.dgtechhealthcare.patient.model

import android.view.View
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PatientAdditionalModel(view : View) {

    val reference = FirebasePresenter(view)

    fun populateView(userType: String, data: PatientDataClass) {
        reference.userReference.child(userType).addValueEventListener(object : ValueEventListener {
            var accountType = ""
            override fun onDataChange(snapshot: DataSnapshot) {
                accountType = snapshot.child("accountType").value.toString()
                if(accountType.compareTo("patient")==0 && userType.compareTo(reference.currentUserId!!)==0){

                } else if(accountType.compareTo("doctor")==0 || userType.compareTo(reference.currentUserId!!)!=0) {
                    data.uploadB.visibility = View.INVISIBLE
                }

                data.fname.setText(snapshot.child("fatherName").value.toString())
                data.mname.setText(snapshot.child("motherName").value.toString())
                data.address.setText(snapshot.child("otherDetes").value.toString())
                data.dname.setText(snapshot.child("doctorName").value.toString())
                data.hname.setText(snapshot.child("hostpitalName").value.toString())
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}