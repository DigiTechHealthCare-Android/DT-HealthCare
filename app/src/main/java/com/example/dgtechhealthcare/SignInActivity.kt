package com.example.dgtechhealthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    lateinit var userEmail : EditText
    lateinit var userPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        auth = FirebaseAuth.getInstance()

        userEmail = findViewById(R.id.emailE)
        userPassword = findViewById(R.id.passE)

        val registerT = findViewById<TextView>(R.id.registerTV)
        registerT.setOnClickListener {
            val i = Intent(this,SignUpActivity::class.java)
            startActivity(i)
        }
    }

    fun signInB(view: View) {
        val email = userEmail.text.toString()
        val password = userPassword.text.toString()

        if(email.isEmpty() || password.isEmpty()) Toast.makeText(this,"Please enter both email and password",Toast.LENGTH_LONG).show()
        else {
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(this,"Welcome!",Toast.LENGTH_LONG).show()
                    val i = Intent(this,MainActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(this,"Error: ${it.exception?.message}",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}