package com.example.dgtechhealthcare.pharmacist.presenter

import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.doctorPrescribeMedicine.TOPIC
import com.example.dgtechhealthcare.pushNotification.FirebaseNotificationService
import com.example.dgtechhealthcare.pushNotification.NotificationData
import com.example.dgtechhealthcare.pushNotification.PushNotification
import com.example.dgtechhealthcare.pushNotification.RetrofitInstance
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class PrepareNotification(view : View) {

    val TAG = "DoctorPrescribe"

    val reference : FirebasePresenter = FirebasePresenter(view)

    fun prepareNotification(notificationMessage : String,userID:String,activity:FragmentActivity){
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("username").value.toString()

                reference.userReference.child(userID).addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val token = snapshot.child("token").value.toString()

                        FirebaseNotificationService.sharedPref = activity?.getSharedPreferences("sharedPref",
                            Context.MODE_PRIVATE)
                        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                        val title = name
                        val message = notificationMessage
                        PushNotification(
                            NotificationData(title,message)
                            , token
                        ).also {
                            sendNotification(it)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}

                })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendNotification(it: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(it)
            if (response.isSuccessful){
                Log.d(TAG,"Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG,response.errorBody().toString())
            }
        } catch (e : Exception){
            Log.e(TAG, e.toString())
        }
    }

}