package com.example.dgtechhealthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.dgtechhealthcare.utils.NetworkUtil
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var auth : FirebaseAuth

    lateinit var spinner : Spinner
    val roles = mutableListOf("Patient","Doctor","Nurse","Pharmacist","Content Manager")

    lateinit var userName : EditText
    lateinit var userEmail : EditText
    lateinit var userPass : EditText

    var roleChoice = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        spinner = findViewById(R.id.roleSpinner)
        spinner.adapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,roles)
        spinner.onItemSelectedListener = this

        userName = findViewById(R.id.userEV)
        userEmail = findViewById(R.id.emailEV)
        userPass = findViewById(R.id.passwordEV)

        val loginT = findViewById<TextView>(R.id.loginT)
        loginT.setOnClickListener {
            val i = Intent(this,SignInActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    fun signUpB(view: View) {

        val nameT = userName.text.toString()
        val emailT = userEmail.text.toString()
        val passT = userPass.text.toString()

        if(nameT.isEmpty()) Toast.makeText(this,"Please enter your name",Toast.LENGTH_LONG).show()
        else if(emailT.isEmpty()) Toast.makeText(this,"Please enter your email!", Toast.LENGTH_SHORT).show()
        else if(passT.isEmpty()) Toast.makeText(this,"Please enter your password!", Toast.LENGTH_SHORT).show()
        else {
            val networkState = NetworkUtil().checkStatus(this,this.intent)
            if (networkState) {
                auth.createUserWithEmailAndPassword(emailT,passT).addOnCompleteListener{
                    if(it.isSuccessful) {
                        val i = Intent(this,SetupActivity::class.java)
                        i.putExtra("role",roleChoice)
                        startActivity(i)
                        finish()
                    }
                }
            } else {}

        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(roles[position])
        {
            "Patient" -> {
                roleChoice = "patient"
            }
            "Doctor" -> {
                roleChoice = "doctor"
            }
            "Nurse" -> {
                roleChoice = "nurse"
            }
            "Pharmacist" -> {
                roleChoice = "pharmacist"
            }
            "Content Manager" -> {
                roleChoice = "manager"
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}