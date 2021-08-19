package com.example.dgtechhealthcare.presenter

import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebasePresenter(val view: View) {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    val currentUserId : String? = auth.currentUser?.uid!!

    val userReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

}