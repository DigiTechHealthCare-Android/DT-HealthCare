package com.example.dgtechhealthcare.signup

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.dgtechhealthcare.utils.SetupActivity
import com.example.dgtechhealthcare.utils.NetworkUtil
import com.google.firebase.auth.FirebaseAuth

class SignUpPresenter {

    val auth = FirebaseAuth.getInstance()
    val viewRef = SignUpActivity()

    fun signUpUser(context: Context,nameT:String,emailT:String,passT:String,roleChoice:String,activity:Activity){
        if(nameT.isEmpty()) viewRef.emptyNameMessage(context)
        else if(emailT.isEmpty()) viewRef.emptyEmailMessage(context)
        else if(passT.isEmpty()) viewRef.emptyPasswordMessage(context)
        else {
            val networkState = NetworkUtil().checkStatus(context,activity.intent)
            if (networkState) {
                auth.createUserWithEmailAndPassword(emailT,passT).addOnCompleteListener{
                    if(it.isSuccessful) {
                        val i = Intent(context, SetupActivity::class.java)
                        i.putExtra("role",roleChoice)
                        val type = context.getSharedPreferences("accountType",
                            AppCompatActivity.MODE_PRIVATE
                        )
                        val editor = type.edit()
                        editor.putString("type",roleChoice)
                        editor.apply()
                        editor.commit()
                        context.startActivity(i)
                        activity.finish()
                    }
                }
            } else {}
        }
    }
}