package com.example.dgtechhealthcare.signup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.signin.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, SignUpContract.View {

    lateinit var auth : FirebaseAuth
    lateinit var presenter : SignUpPresenter

    lateinit var spinner : Spinner
    val roles = mutableListOf("Patient","Doctor","Nurse","Pharmacist","Content Manager")

    var roleChoice = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        presenter = SignUpPresenter()

        spinner = findViewById(R.id.roleSpinner)
        spinner.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item,roles)
        spinner.onItemSelectedListener = this

        val loginT = findViewById<TextView>(R.id.loginT)
        loginT.setOnClickListener {
            val i = Intent(this, SignInActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    fun signUpB(view: View) {

        val nameT = userEV.text.toString()
        val emailT = emailEV.text.toString()
        val passT = passwordEV.text.toString()

        presenter.signUpUser(this,nameT,emailT,passT,roleChoice,this)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(roles[position])
        {
            "Patient" -> { roleChoice = "patient" }
            "Doctor" -> { roleChoice = "doctor" }
            "Nurse" -> { roleChoice = "nurse" }
            "Pharmacist" -> { roleChoice = "pharmacist" }
            "Content Manager" -> { roleChoice = "manager" }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun emptyNameMessage(context: Context) {
        Toast.makeText(context,R.string.name_please, Toast.LENGTH_LONG).show()
    }

    override fun emptyEmailMessage(context: Context) {
        Toast.makeText(context,R.string.email_please, Toast.LENGTH_SHORT).show()
    }

    override fun emptyPasswordMessage(context: Context) {
        Toast.makeText(context,R.string.password_please, Toast.LENGTH_SHORT).show()
    }
}