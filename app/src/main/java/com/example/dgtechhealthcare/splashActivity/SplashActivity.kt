package com.example.dgtechhealthcare.splashActivity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.signup.SignUpActivity
import com.example.dgtechhealthcare.utils.NetworkUtil
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity(),SplashContract.View {

    lateinit var iv_note : ImageView
    var type : String? = ""

    lateinit var auth : FirebaseAuth
    lateinit var loadingBar : ProgressDialog

    var checkUser : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val test : String? = intent.getStringExtra("test")

        auth = FirebaseAuth.getInstance()
        loadingBar = ProgressDialog(this)
        iv_note = findViewById(R.id.iv_note)
        checkUser = getSharedPreferences("check", MODE_PRIVATE)

        iv_note.alpha =0f
        iv_note.animate().setDuration(1500).alpha(1f).withEndAction{
            val networkState = NetworkUtil().checkStatus(this,this.intent)
            if (networkState){
                if(checkUser?.getBoolean("firstrun",true)!!) {
                    checkUser?.edit()?.putBoolean("firstrun",false)?.commit()
                    val i = Intent(this, SignUpActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    SplashPresenter().splashLogIn(test,this,this@SplashActivity)
                }
            } else { }
        }
    }

    override fun welcomeTextDoctor(context: Context) {
        //Toast.makeText(context,R.string.doctor_welcome, Toast.LENGTH_LONG).show()
    }

    override fun welcomeTextNurse(context: Context) {
        //Toast.makeText(context,R.string.nurse_welcome, Toast.LENGTH_LONG).show()
    }

    override fun welcomeTextPharmacist(context: Context) {
        //Toast.makeText(context,R.string.phrama_welcome, Toast.LENGTH_LONG).show()
    }

    override fun welcomeTextContentManager(context: Context) {
        //Toast.makeText(context,R.string.manager_welcome, Toast.LENGTH_LONG).show()
    }
}