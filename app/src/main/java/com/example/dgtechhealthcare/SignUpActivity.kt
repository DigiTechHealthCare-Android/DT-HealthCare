package com.example.dgtechhealthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var auth : FirebaseAuth

    lateinit var spinner : Spinner
    val roles = mutableListOf("Patient","Doctor","Nurse","Pharmacist","Content Manager")

    lateinit var userName : EditText
    lateinit var userEmail : EditText
    lateinit var userPass : EditText
    lateinit var userConfirm : EditText

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
      //  userConfirm = findViewById(R.id.conPassEV)

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
      //  val confirmT = userConfirm.text.toString()

        if(nameT.isEmpty()) Toast.makeText(this,"Please enter your name",Toast.LENGTH_LONG).show()
        else if(emailT.isEmpty()) Toast.makeText(this,"Please enter your email!", Toast.LENGTH_SHORT).show()
        else if(passT.isEmpty()) Toast.makeText(this,"Please enter your password!", Toast.LENGTH_SHORT).show()
        //else if(confirmT.isEmpty()) Toast.makeText(this,"Please confirm your password!",Toast.LENGTH_SHORT).show()
        //else if(passT.compareTo(confirmT) != 0) Toast.makeText(this,"password and confirm password do not match",Toast.LENGTH_SHORT).show()
        else {
            auth.createUserWithEmailAndPassword(emailT,passT).addOnCompleteListener{
                if(it.isSuccessful) {
                    val i = Intent(this,SetupActivity::class.java)
                    i.putExtra("role",roleChoice)
                    startActivity(i)
                    finish()
                }
                else {
                    Toast.makeText(this, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
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
            /*"Patient" -> {
                val frag = PatientRegistrationFragment()
                supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
            }
            "Doctor" -> {
                val frag = DoctorRegistrationFragment()
                supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
            }
            "Nurse" -> {
                val frag = NurseRegistrationFragment()
                supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
            }
            "Pharmacist" -> {
                val frag = PharmacistRegistrationFragment()
                supportFragmentManager.beginTransaction().replace(R.id.setupFrame,frag).commit()
            }*/
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}