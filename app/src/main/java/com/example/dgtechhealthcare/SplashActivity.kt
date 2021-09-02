package com.example.dgtechhealthcare

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.dgtechhealthcare.utils.NetworkUtil
import com.example.dgtechhealthcare.view.PharmacistDrawerNavigationActivity
import com.example.dgtechhealthcare.view.ContentManagerDrawerNavigationActivity
import com.example.dgtechhealthcare.view.DoctorDrawerNavigationActivity
import com.example.dgtechhealthcare.view.NurseDrawerNavigationActivity
import com.example.dgtechhealthcare.view.PatientDrawerNavigationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SplashActivity : AppCompatActivity() {

    lateinit var iv_note : ImageView
    var type : String? = ""

    lateinit var auth : FirebaseAuth
    lateinit var loadingBar : ProgressDialog

    var checkUser : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val test : String? = intent.getStringExtra("test")

        auth = FirebaseAuth.getInstance()
        loadingBar = ProgressDialog(this)
        iv_note = findViewById(R.id.iv_note)
        checkUser = getSharedPreferences("check", MODE_PRIVATE)

        iv_note.alpha =0f
        iv_note.animate().setDuration(1500).alpha(1f).withEndAction{
            val networkState = NetworkUtil().checkStatus(this,this.intent)
            if (networkState){
                if(checkUser?.getBoolean("firstrun",true)!!) {
                    checkUser?.edit()?.putBoolean("firstrun",false)?.commit()
                    val i = Intent(this,SignUpActivity::class.java)
                    startActivity(i)
                    finish()
                } else {

                    if(auth.currentUser !=null){

                        FirebaseMessaging.getInstance().token.addOnCompleteListener {
                            if (it.isSuccessful){
                                val token = it.result.toString()
                                FirebaseDatabase.getInstance().reference.child("Users")
                                    .child(auth.currentUser!!.uid).child("token").setValue(token)
                            }
                        }

                        val job = CoroutineScope(Dispatchers.Default).launch {
                            val result = CoroutineScope(Dispatchers.Default).async {
                                FlagTask().execute()
                            }
                            result.await()!!
                            Thread.sleep(4000)

                            if(type?.compareTo("patient") ==0){
                                val i = Intent(this@SplashActivity, PatientDrawerNavigationActivity::class.java)
                                if(test?.compareTo("doctor")==0){
                                    i.putExtra("test","doctor")
                                }
                                startActivity(i)
                                loadingBar.dismiss()
                                finish()
                            }else if(type?.compareTo("doctor") == 0) {
                                loadingBar.dismiss()
                                runOnUiThread {
                                    Toast.makeText(this@SplashActivity,"Welcome Doctor", Toast.LENGTH_LONG).show()
                                    val i = Intent(this@SplashActivity, DoctorDrawerNavigationActivity::class.java)
                                    startActivity(i)
                                    finish()
                                }
                            } else if(type?.compareTo("nurse") == 0 ) {
                                loadingBar.dismiss()
                                runOnUiThread {
                                    Toast.makeText(this@SplashActivity,"Welcome", Toast.LENGTH_LONG).show()
                                    val i = Intent(this@SplashActivity, NurseDrawerNavigationActivity::class.java)
                                    startActivity(i)
                                    finish()
                                }
                            } else if(type?.compareTo("pharmacist") == 0) {
                                loadingBar.dismiss()
                                runOnUiThread {
                                    Toast.makeText(this@SplashActivity,"Shop's open", Toast.LENGTH_LONG).show()
                                    val i = Intent(this@SplashActivity, PharmacistDrawerNavigationActivity::class.java)
                                    if (test?.compareTo("doctor")!=0)
                                        i.putExtra("test",test)
                                    startActivity(i)
                                    finish()
                                }
                            } else if(type?.compareTo("contentManager")==0){
                                loadingBar.dismiss()
                                runOnUiThread {
                                    Toast.makeText(this@SplashActivity,"Time to post content", Toast.LENGTH_LONG).show()
                                    val i = Intent(this@SplashActivity,
                                        ContentManagerDrawerNavigationActivity::class.java)
                                    startActivity(i)
                                    finish()
                                }
                            }
                        }

                    } else {
                        val i = Intent(this,SignInActivity::class.java)
                        startActivity(i)
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                        finish()
                    }
                }
            } else {
            }

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
            } else Toast.makeText(this@SplashActivity,"Error", Toast.LENGTH_LONG).show()}
    }

    override fun onResume() {
        super.onResume()
    }
}