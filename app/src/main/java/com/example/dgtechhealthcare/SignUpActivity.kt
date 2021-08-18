package com.example.dgtechhealthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    lateinit var userName : EditText
    lateinit var userEmail : EditText
    lateinit var userPass : EditText
    lateinit var userConfirm : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        userName = findViewById(R.id.userEV)
        userEmail = findViewById(R.id.emailEV)
        userPass = findViewById(R.id.passwordEV)
        userConfirm = findViewById(R.id.conPassEV)

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
        val confirmT = userConfirm.text.toString()

        if(nameT.isEmpty()) Toast.makeText(this,"Please enter your name",Toast.LENGTH_LONG).show()
        else if(emailT.isEmpty()) Toast.makeText(this,"Please enter your email!", Toast.LENGTH_SHORT).show()
        else if(passT.isEmpty()) Toast.makeText(this,"Please enter your password!", Toast.LENGTH_SHORT).show()
        else if(confirmT.isEmpty()) Toast.makeText(this,"Please confirm your password!",Toast.LENGTH_SHORT).show()
        else if(passT.compareTo(confirmT) != 0) Toast.makeText(this,"password and confirm password do not match",Toast.LENGTH_SHORT).show()
        else {
            auth.createUserWithEmailAndPassword(emailT,passT).addOnCompleteListener{
                if(it.isSuccessful) {
                    val i = Intent(this,SetupActivity::class.java)
                    startActivity(i)
                    finish()
                }
                else {
                    Toast.makeText(this, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}