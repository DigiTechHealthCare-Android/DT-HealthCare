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


        val logout = findViewById<Button>(R.id.logoutB)

        logout.setOnClickListener {
            auth.signOut()
            val i = Intent(this,SignInActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(roles[position])
        {
            "Patient" -> {}
            "Doctor" -> {}
            "Nurse" -> {}
            "Pharmacist" -> {}
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}