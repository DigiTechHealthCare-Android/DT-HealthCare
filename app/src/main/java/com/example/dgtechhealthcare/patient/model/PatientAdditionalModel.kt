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

                if(snapshot.hasChild("fatherName")) data.fname.setText(snapshot.child("fatherName").value.toString())
                else data.fname.setText("N/A")

                if (snapshot.hasChild("motherName")) data.mname.setText(snapshot.child("motherName").value.toString())
                else data.mname.setText("N/A")

                if (snapshot.hasChild("otherDetes")) data.address.setText(snapshot.child("otherDetes").value.toString())
                else data.address.setText("N/A")

                if (snapshot.hasChild("docotorName")) data.dname.setText(snapshot.child("doctorName").value.toString())
                else data.dname.setText("N/A")

                if (snapshot.hasChild("hospitalName")) data.hname.setText(snapshot.child("hostpitalName").value.toString())
                else data.hname.setText("N/A")
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}