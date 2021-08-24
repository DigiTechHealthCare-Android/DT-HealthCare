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
    val contentPostRef : StorageReference = FirebaseStorage.getInstance().getReference().child("contentMedia")

    val userReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
    val doctorReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Doctors")
    val pharmaReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Pharmacists")
    val managerReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("ContentManager")
    val articleReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Articles")

}