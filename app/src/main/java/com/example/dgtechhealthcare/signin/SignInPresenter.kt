package com.example.dgtechhealthcare.signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.dgtechhealthcare.utils.NetworkUtil
import com.example.dgtechhealthcare.utils.SignInRetrofit
import com.example.dgtechhealthcare.view.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class SignInPresenter {

    val auth = FirebaseAuth.getInstance()
    var type : String? = ""
    var context1 : Context? = null

    fun signInUser(
        email: String,
        password: String,
        context: Context,
        signInActivity: SignInActivity
    ){
        context1 = context
        if(email.isEmpty() || password.isEmpty()) Toast.makeText(context,"Please enter both email and password",
            Toast.LENGTH_LONG).show()
        else {
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if(it.isSuccessful) {
                    sendToDashboard(context,signInActivity)
                } else {
                    Toast.makeText(context,"Error: ${it.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sendToDashboard(context: Context, activity: Activity){
        val networkState = NetworkUtil().checkStatus(context,activity.intent)
        if (networkState){
            if(auth.currentUser != null) {

                SignInActivity().showLoadingBar(context)

                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (it.isSuccessful){
                        val token = it.result.toString()
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(auth.currentUser!!.uid).child("token").setValue(token)
                    }
                }

                val job = CoroutineScope(Dispatchers.Default).launch {
                    val result = CoroutineScope(Dispatchers.Default).async {
                        //FlagTask().execute()
                        val request = SignInRetrofit.getInstance().getAccountType(auth.currentUser?.uid!!)
                        request.enqueue(AccountCallback())
                    }
                    result.await()!!
                    Thread.sleep(4000)

                    if(type?.compareTo("patient") ==0){
                        activity.runOnUiThread {
                            val i = Intent(context, PatientDrawerNavigationActivity::class.java)
                            context.startActivity(i)
                            SignInActivity().dismissLoadingBar(context)
                            activity.finish()
                        }
                    }else if(type?.compareTo("doctor") == 0) {
                        activity.runOnUiThread {
                            SignInActivity().dismissLoadingBar(context)
                            //Toast.makeText(context,"Welcome Doctor",Toast.LENGTH_SHORT).show()
                            val i = Intent(context, DoctorDrawerNavigationActivity::class.java)
                            activity.startActivity(i)
                            activity.finish()
                        }
                    } else if(type?.compareTo("nurse") == 0 ) {
                        activity.runOnUiThread {
                            SignInActivity().dismissLoadingBar(context)
                            //Toast.makeText(context,"Welcome",Toast.LENGTH_SHORT).show()
                            val i = Intent(context, NurseDrawerNavigationActivity::class.java)
                            activity.startActivity(i)
                            activity.finish()
                        }
                    } else if(type?.compareTo("pharmacist") == 0) {
                        activity.runOnUiThread {
                            SignInActivity().dismissLoadingBar(context)
                            //Toast.makeText(context,"Shop's open",Toast.LENGTH_SHORT).show()
                            val i = Intent(context, PharmacistDrawerNavigationActivity::class.java)
                            activity.startActivity(i)
                            activity.finish()
                        }
                    } else if(type?.compareTo("contentManager")==0){
                        activity.runOnUiThread {
                            SignInActivity().dismissLoadingBar(context)
                            //Toast.makeText(context,"Time to post content",Toast.LENGTH_SHORT).show()
                            val i = Intent(context,
                                ContentManagerDrawerNavigationActivity::class.java)
                            activity.startActivity(i)
                            activity.finish()
                        }
                    }
                }
            } else {}
        } else {}

    }

    inner class AccountCallback: retrofit2.Callback<Userdata>{

        override fun onResponse(call: Call<Userdata>, response: Response<Userdata>) {
            if (response.isSuccessful){
                val details = response.body()

                SignInActivity().signInTest(details,context1)
                type = details?.accountType
            }
        }
        override fun onFailure(call: Call<Userdata>, t: Throwable) {
            SignInActivity().signInFailure(context1)
        }
    }
}