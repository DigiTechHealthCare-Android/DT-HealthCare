package com.example.dgtechhealthcare.utils

import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebasePresenter(val view: View) {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    val currentUserId : String? = auth.currentUser?.uid!!

    val userProfileImgRef : StorageReference = FirebaseStorage.getInstance().getReference().child("profileImgs")
    val userReportRef : StorageReference = FirebaseStorage.getInstance().getReference().child("userReports")

    val userReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
    val doctorReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Doctors")

}