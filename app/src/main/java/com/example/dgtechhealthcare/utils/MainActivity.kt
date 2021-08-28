package com.example.dgtechhealthcare.utils

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.SignInActivity
import com.example.dgtechhealthcare.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    var type : String? = ""

    lateinit var signUpButton : Button

    //lateinit var reference : FirebasePresenter
    lateinit var loadingBar : ProgressDialog

    lateinit var mauth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //reference = FirebasePresenter(android.view.View(this))
        mauth = FirebaseAuth.getInstance()
        loadingBar = ProgressDialog(this)

        /*if(mauth.currentUser != null) {
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
                    val i = Intent(this@MainActivity, PatientDrawerNavigationActivity::class.java)
                    startActivity(i)
                    loadingBar.dismiss()
                    finish()
                }else if(type?.compareTo("doctor") == 0) {
                    loadingBar.dismiss()
                    runOnUiThread {
                        Toast.makeText(this@MainActivity,"Welcome Doctor",Toast.LENGTH_LONG).show()
                        val i = Intent(this@MainActivity, DoctorDrawerNavigationActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                } else if(type?.compareTo("nurse") == 0 ) {
                    loadingBar.dismiss()
                    runOnUiThread {
                        Toast.makeText(this@MainActivity,"Welcome",Toast.LENGTH_LONG).show()
                        val i = Intent(this@MainActivity, NurseDrawerNavigationActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                } else if(type?.compareTo("pharmacist") == 0) {
                    loadingBar.dismiss()
                    runOnUiThread {
                        Toast.makeText(this@MainActivity,"Shop's open",Toast.LENGTH_LONG).show()
                        val i = Intent(this@MainActivity, PharmacistNavigationActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                }
            }
        } else {
//            val i = Intent(this,SignInActivity::class.java)
//            startActivity(i)
//            finish()
        }*/

        signUpButton = findViewById(R.id.signupB)

        signUpButton.setOnClickListener {
            val i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
        }
    }

    fun onButtonClick(view: View) {
        val i = Intent(this, SignInActivity::class.java)
        startActivity(i)
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
            return mauth.currentUser?.uid?.let { getResponse(it) } ?: ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(result?.isNotEmpty()!!){
                val responseObj = JSONObject(result)
                val accountType = responseObj.getString("accountType")
                type = accountType
            } else Toast.makeText(this@MainActivity,"Error", Toast.LENGTH_LONG).show()}
        }
}