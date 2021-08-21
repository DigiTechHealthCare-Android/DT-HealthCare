package com.example.dgtechhealthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth

class SetupActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var auth : FirebaseAuth

    lateinit var spinner : Spinner
    val roles = mutableListOf("Patient","Doctor","Nurse","Pharmacist")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        auth = FirebaseAuth.getInstance()

        spinner = findViewById(R.id.setupSpinner)
        spinner.adapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,roles)
        spinner.onItemSelectedListener = this

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
        }

        /*val logout = findViewById<Button>(R.id.logoutB)

        logout.setOnClickListener {
            auth.signOut()
            val i = Intent(this,SignInActivity::class.java)
            startActivity(i)
            finish()
        }*/
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(roles[position])
        {
            "Patient" -> {
                val frag = PatientRegistrationFragment()
                supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
            }
            "Doctor" -> {
                val frag = DoctorRegistrationFragment()
                supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
            }
            "Nurse" -> {
                /*val frag = NurseRegistrationFragment()
                supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()*/
            }
            "Pharmacist" -> {
                val frag = PharmacistRegistrationFragment()
                supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}