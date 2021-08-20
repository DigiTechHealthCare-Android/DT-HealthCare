package com.example.dgtechhealthcare

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {

    lateinit var iv_note : ImageView

    var checkUser : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        iv_note = findViewById(R.id.iv_note)
        checkUser = getSharedPreferences("check", MODE_PRIVATE)

        iv_note.alpha =0f
        iv_note.animate().setDuration(1500).alpha(1f).withEndAction{
            if(checkUser?.getBoolean("firstrun",true)!!) {
                checkUser?.edit()?.putBoolean("firstrun",false)?.commit()
                val i = Intent(this,SignUpActivity::class.java)
                startActivity(i)
                finish()
            } else {
                //val i = Intent(this,MainActivity::class.java)
                val i = Intent(this,SignInActivity::class.java)
                startActivity(i)
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}