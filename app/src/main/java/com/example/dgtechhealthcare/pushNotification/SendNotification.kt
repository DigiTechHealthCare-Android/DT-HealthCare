package com.example.dgtechhealthcare.pushNotification

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject

class SendNotification {

    fun getToken(message:String,userID : String){

        val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
            .child(userID)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val token = snapshot.child("token").value.toString()
                    val name = snapshot.child("usernmae").value.toString()

                    val to = JSONObject()
                    val data = JSONObject()

                    data.put("uid",userID)
                    data.put("message",message)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}