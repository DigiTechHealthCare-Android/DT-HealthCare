package com.example.dgtechhealthcare.pharmacist.model

import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.pharmacist.presenter.PrepareNotification
import com.example.dgtechhealthcare.pharmacist.presenter.RequestDescriptionPresenter
import com.example.dgtechhealthcare.pharmacist.view.RequestDescriptionFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class RequestDescriptionModel(view : View) {

    val reference = FirebasePresenter(view)
    val notificationReference = PrepareNotification(view)
    //val view = RequestDescriptionFragment()

    fun populateDescription(type: String, descriptionData: DescriptionData,
        requireActivity: FragmentActivity, userID: String) {

        val presenter = RequestDescriptionPresenter(View(requireActivity))

        var m1 = ""
        var m2 = ""
        var m3 = ""
        var m4 = ""

        reference.userReference.child(userID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                descriptionData.name.setText(snapshot.child("username").value.toString())
                Picasso.get().load(snapshot.child("profileImage").value.toString()).into(descriptionData.image)
                if(type.compareTo("requestHistory")==0){
                    reference.pharmaReference.child(reference.currentUserId!!).child("requestHistory").child(userID).addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            m1 = snapshot.child("med1").value.toString()
                            m2 = snapshot.child("med2").value.toString()
                            m3 = snapshot.child("med3").value.toString()
                            m4 = snapshot.child("med4").value.toString()

                            descriptionData.med1.setText(m1)
                            descriptionData.med2.setText(m2)
                            descriptionData.med3.setText(m3)
                            descriptionData.med4.setText(m4)
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                } else {
                    reference.pharmaReference.child(reference.currentUserId!!).child("requests").child(userID).addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            m1 = snapshot.child("med1").value.toString()
                            m2 = snapshot.child("med2").value.toString()
                            m3 = snapshot.child("med3").value.toString()
                            m4 = snapshot.child("med4").value.toString()

                            descriptionData.med1.setText(m1)
                            descriptionData.med2.setText(m2)
                            descriptionData.med3.setText(m3)
                            descriptionData.med4.setText(m4)
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }

            }
            override fun onCancelled(error: DatabaseError) {}

        })

        descriptionData.acceptB.setOnClickListener {
            val hashMap = HashMap<String,Any>()
            hashMap["med1"] = m1
            hashMap["med2"] = m2
            hashMap["med3"] = m3
            hashMap["med4"] = m4

            reference.pharmaReference.child(reference.currentUserId!!).child("requestHistory").child(userID).updateChildren(hashMap).addOnCompleteListener {
                if(it.isSuccessful){
                    presenter.requestApproved(requireActivity)
                    requireActivity?.supportFragmentManager?.popBackStack()
                    reference.pharmaReference.child(reference.currentUserId!!).child("requests").child(userID).removeValue().addOnCompleteListener {
                        if(it.isSuccessful) notificationReference.prepareNotification("Request Approved",userID,requireActivity)
                    }
                }
            }
        }

        descriptionData.declineB.setOnClickListener {
            reference.pharmaReference.child(reference.currentUserId!!).child("requests").child(userID).removeValue().addOnCompleteListener {
                presenter.requestDeclined(requireActivity)
                requireActivity?.supportFragmentManager?.popBackStack()
                if(it.isSuccessful) notificationReference.prepareNotification("Request Declined",userID,requireActivity)
            }
        }
    }

}