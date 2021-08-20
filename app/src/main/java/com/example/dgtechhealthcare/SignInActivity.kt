package com.example.dgtechhealthcare

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.dgtechhealthcare.view.DoctorNavigationActivity
import com.example.dgtechhealthcare.view.NurseNavigationActivity
import com.example.dgtechhealthcare.view.PharmacistNavigationActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SignInActivity : AppCompatActivity() {

    var type : String? = ""
    lateinit var auth : FirebaseAuth
    lateinit var loadingBar : ProgressDialog

    lateinit var userEmail : EditText
    lateinit var userPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        auth = FirebaseAuth.getInstance()
        loadingBar = ProgressDialog(this)

        userEmail = findViewById(R.id.emailE)
        userPassword = findViewById(R.id.passE)

        val registerT = findViewById<TextView>(R.id.registerTV)
        registerT.setOnClickListener {
            val i = Intent(this,SignUpActivity::class.java)
            startActivity(i)
        }
    }

    override fun onResume() {
        super.onResume()

        sendToDashboard()
    }

    fun sendToDashboard(){
        if(auth.currentUser != null) {
            loadingBar.setTitle("Authenticating")
            loadingBar.setMessage("Please wait!")
            loadingBar.setCanceledOnTouchOutside(false)
            loadingBar.show()

            val job = CoroutineScope(Dispatchers.Default).launch {
                val result = CoroutineScope(Dispatchers.Default).async {
                    FlagTask().execute()
                }
                result.await()!!
                Thread.sleep(4000)

                if(type?.compareTo("patient") ==0){
                    val i = Intent(this@SignInActivity, PatientsNavigationActivity::class.java)
                    startActivity(i)
                    loadingBar.dismiss()
                    finish()
                }else if(type?.compareTo("doctor") == 0) {
                    loadingBar.dismiss()
                    runOnUiThread {
                        Toast.makeText(this@SignInActivity,"Welcome Doctor",Toast.LENGTH_LONG).show()
                        val i = Intent(this@SignInActivity, DoctorNavigationActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                } else if(type?.compareTo("nurse") == 0 ) {
                    loadingBar.dismiss()
                    runOnUiThread {
                        Toast.makeText(this@SignInActivity,"Welcome",Toast.LENGTH_LONG).show()
                        val i = Intent(this@SignInActivity, NurseNavigationActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                } else if(type?.compareTo("pharmacist") == 0) {
                    loadingBar.dismiss()
                    runOnUiThread {
                        Toast.makeText(this@SignInActivity,"Shop's open",Toast.LENGTH_LONG).show()
                        val i = Intent(this@SignInActivity, PharmacistNavigationActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                }
            }
        } else {
//            val i = Intent(this,SignInActivity::class.java)
//            startActivity(i)
//            finish()
        }
    }

    fun getResponse(id : String) : String? {

        val urls = "https://testdatabase-8dfa3-default-rtdb.firebaseio.com/Users/$id.json"
        val url = URL(urls)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        if(connection.responseCode == 200){
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var line = reader.readLine()
            var response = ""
            while(line != null) {
                response += line
                line = reader.readLine()
            }
            return response
        } else {
            Log.d("MainActivity","Error: ${connection.responseCode}, ${connection.responseMessage}")
        }
        return  null
    }

    inner class FlagTask : AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg params: String?): String {
            return auth.currentUser?.uid?.let { getResponse(it) } ?: ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(result?.isNotEmpty()!!){
                val responseObj = JSONObject(result)
                val accountType = responseObj.getString("accountType")
                type = accountType
            } else Toast.makeText(this@SignInActivity,"Error", Toast.LENGTH_LONG).show()}
    }

    fun signInB(view: View) {
        val email = userEmail.text.toString()
        val password = userPassword.text.toString()

        if(email.isEmpty() || password.isEmpty()) Toast.makeText(this,"Please enter both email and password",Toast.LENGTH_LONG).show()
        else {
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if(it.isSuccessful) {
                    /*Toast.makeText(this,"Welcome!",Toast.LENGTH_LONG).show()
                    val i = Intent(this,MainActivity::class.java)
                    startActivity(i)
                    finish()*/
                    sendToDashboard()
                } else {
                    Toast.makeText(this,"Error: ${it.exception?.message}",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}