package com.example.dgtechhealthcare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class SetupActivity : AppCompatActivity(){

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        auth = FirebaseAuth.getInstance()

        val extra : Bundle? = intent.extras
        val role = extra?.getString("role")
        if(role?.compareTo("patient")==0){
            val frag = PatientRegistrationFragment()
            supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
        } else if(role?.compareTo("doctor")==0){
            val frag = DoctorRegistrationFragment()
            supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
        } else if(role?.compareTo("nurse")==0) {
            val frag = NurseRegistrationFragment()
            supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
        } else if(role?.compareTo("pharmacist")==0){
            val frag = PharmacistRegistrationFragment()
            supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
        } else if(role?.compareTo("manager")==0){
            val frag = ContentManagerRegistrationFragment()
            supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
        }
    }
}