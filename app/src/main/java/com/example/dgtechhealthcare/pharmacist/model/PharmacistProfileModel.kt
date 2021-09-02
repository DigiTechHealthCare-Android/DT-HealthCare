package com.example.dgtechhealthcare.pharmacist.model

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class PharmacistProfileModel(view : View) {

    val reference = FirebasePresenter(view)

    fun populateProfile(data: PharmacistProfileData, activity: FragmentActivity?) {
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pharmacist = snapshot.getValue(PharmacistData::class.java)

                if (pharmacist?.profileImage == null){
                    data.profileImage.setImageResource(R.drawable.profile)
                }
                else{
                    Picasso.get().load(pharmacist?.profileImage).into(data.profileImage)
                }
                data.username.text = pharmacist?.username
                data.name.text = pharmacist?.pharmacyName
                data.contact.text = "${pharmacist?.contact}"
                data.location.text = "${pharmacist?.location}"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}