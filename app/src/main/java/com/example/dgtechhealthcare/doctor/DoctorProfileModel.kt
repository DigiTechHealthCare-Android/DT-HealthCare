package com.example.dgtechhealthcare.doctor

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class DoctorProfileModel(view : View) {

    val reference = FirebasePresenter(view)

    fun populateDoctorProfile(data: DoctorProfileData) {

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")) {
                    val img = snapshot.child("profileImage").value.toString()
                    Picasso.get().load(img).into(data.profileIV)
                }

                data.username.text = snapshot.child("username").value.toString()
                data.useremail.text = snapshot.child("email").value.toString()
                data.userhospital.text = snapshot.child("hospital").value.toString()
                data.userspecial.text = snapshot.child("specialization").value.toString()
                data.usercontact.text = snapshot.child("contact").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    fun uploadToStorage(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri, requireActivity: FragmentActivity) {
        val resultUri = imgUri
        val path = reference.userProfileImgRef.child("$currentUserId.jpg")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(requireActivity,"Profile image changed", Toast.LENGTH_SHORT).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(currentUserId!!).child("profileImage").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(requireActivity,"Image stored", Toast.LENGTH_SHORT).show()
                    }
                }
            } else Toast.makeText(requireActivity,"Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }
}