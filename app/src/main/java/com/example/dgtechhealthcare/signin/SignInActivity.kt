package com.example.dgtechhealthcare.signin

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.signup.SignUpActivity
import com.example.dgtechhealthcare.view.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity(),SignInContract.View {

    var type : String? = ""
    lateinit var auth : FirebaseAuth
    var loadingBar : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        auth = FirebaseAuth.getInstance()
        loadingBar = ProgressDialog(this)
        //reference = FirebasePresenter(View(this))

        val registerT = findViewById<TextView>(R.id.registerTV)
        registerT.setOnClickListener {
            val i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
        }

        val forgotPassT = findViewById<TextView>(R.id.forgotPassTV)
        forgotPassT.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogLayout = layoutInflater.inflate(R.layout.forgot_password_layout,null)
            builder.setView(dialogLayout)
            var email = dialogLayout.findViewById<EditText>(R.id.forgotPassEV)

            with(builder){
                setTitle("Enter your registered Email ID")
                setPositiveButton("Ok"){dialog,which ->
                    val userEmail = email.text.toString()
                    if (!userEmail.isNullOrEmpty())
                    {
                        auth.sendPasswordResetEmail(userEmail).addOnCompleteListener {
                            if(it.isSuccessful) Toast.makeText(this@SignInActivity,R.string.password_reset,Toast.LENGTH_LONG).show()
                        }
                    }
                }
                setNegativeButton("Cancel"){dialog,which -> }//Toast.makeText(this@SignInActivity,R.string.error,Toast.LENGTH_LONG).show()}
            }
            builder.show()
        }
    }

    fun signInB(view: View) {
        val email = emailE.text.toString()
        val password = passE.text.toString()

        SignInPresenter().signInUser(email,password,this,this@SignInActivity)
    }

    override fun showLoadingBar(context: Context) {
        loadingBar = ProgressDialog(context)
        loadingBar!!.setTitle(R.string.authenitcate)
        loadingBar!!.setMessage("Please wait!")
        loadingBar!!.setCanceledOnTouchOutside(false)
        loadingBar!!.show()
        //loadingBar = null
    }

    override fun dismissLoadingBar(context: Context) {
        loadingBar = ProgressDialog(context)
        loadingBar!!.dismiss()
        //loadingBar = null
    }

    override fun signInFailure(context: Context?) {
        Toast.makeText(context,R.string.error,Toast.LENGTH_SHORT).show()
    }

    override fun signInTest(details: Userdata?, context: Context?) {
        //Toast.makeText(context,"$type, ${details?.accountType}",Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingBar?.dismiss()
        if (loadingBar != null){
            loadingBar!!.dismiss()
            loadingBar = null
        }
    }
}
