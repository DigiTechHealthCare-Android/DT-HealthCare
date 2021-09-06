package com.example.dgtechhealthcare.splashActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.dgtechhealthcare.signin.SignInActivity
import com.example.dgtechhealthcare.signin.Userdata
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

class SplashPresenter {

    val auth = FirebaseAuth.getInstance()
    var type : String? = ""
    val view = SplashActivity()

    fun splashLogIn(test :String? = null, context: Context, activity: SplashActivity){

        if(auth.currentUser !=null){

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
                    val i = Intent(context, PatientDrawerNavigationActivity::class.java)
                    if(test?.compareTo("doctor")==0){
                        i.putExtra("test","doctor")
                    }
                    context.startActivity(i)
                    activity.finish()
                }else if(type?.compareTo("doctor") == 0) {
                    activity.runOnUiThread {
                        view.welcomeTextDoctor(context)
                        val i = Intent(context, DoctorDrawerNavigationActivity::class.java)
                        context.startActivity(i)
                        activity.finish()
                    }
                } else if(type?.compareTo("nurse") == 0 ) {
                    activity.runOnUiThread {
                        view.welcomeTextNurse(context)
                        val i = Intent(context, NurseDrawerNavigationActivity::class.java)
                        context.startActivity(i)
                        activity.finish()
                    }
                } else if(type?.compareTo("pharmacist") == 0) {
                    activity.runOnUiThread {
                        view.welcomeTextPharmacist(context)
                        val i = Intent(context, PharmacistDrawerNavigationActivity::class.java)
                        if (test?.compareTo("doctor")!=0)
                            i.putExtra("test",test)
                        context.startActivity(i)
                        activity.finish()
                    }
                } else if(type?.compareTo("contentManager")==0){
                    activity.runOnUiThread {
                        view.welcomeTextContentManager(context)
                        val i = Intent(context, ContentManagerDrawerNavigationActivity::class.java)
                        context.startActivity(i)
                        activity.finish()
                    }
                }
            }
        } else {
            val i = Intent(context, SignInActivity::class.java)
            context.startActivity(i)
            //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            activity.finish()
        }


    }

    inner class AccountCallback: retrofit2.Callback<Userdata>{

        override fun onResponse(call: Call<Userdata>, response: Response<Userdata>) {
            if (response.isSuccessful){
                val details = response.body()
                type = details?.accountType
            }
        }
        override fun onFailure(call: Call<Userdata>, t: Throwable) {}
    }
}