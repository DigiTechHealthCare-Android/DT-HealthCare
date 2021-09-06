package com.example.dgtechhealthcare.patient.presenter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.utils.ViewImageActivity
import com.example.dgtechhealthcare.utils.ViewPdfActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL
import java.net.URLConnection

class PatientViewReportPresenter {

    fun showReport(reference:FirebasePresenter,userID:String,activity:FragmentActivity,context: Context,reportType:String){
        var report = ""
        reference.userReference.child(userID).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(reportType)) {
                    report = snapshot.child(reportType).value.toString()
                    val options = arrayOf<CharSequence>("Download","View","Cancel")
                    val builder : AlertDialog.Builder = AlertDialog.Builder(activity)
                    builder.setTitle("Do you want to?")
                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if(which == 0) {
                            val i = Intent(Intent.ACTION_VIEW, Uri.parse(report))
                            context.startActivity(i)
                        }
                        if(which == 1) {
                            var connection : URLConnection? = null
                            try{
                                connection = URL(report).openConnection()
                            } catch (e : IOException){
                                e.printStackTrace()
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                val contentType = connection?.getHeaderField("Content-Type")
                                val img = contentType?.startsWith("image/")
                                if(img!!){
                                    activity?.runOnUiThread {
                                        val i = Intent(activity, ViewImageActivity::class.java)
                                        i.putExtra("url",report)
                                        context.startActivity(i)
                                    }
                                } else {
                                    val i = Intent(activity, ViewPdfActivity::class.java)
                                    i.putExtra("url",report)
                                    context.startActivity(i)
                                }
                            }
                        }
                    })
                    builder.show()
                } else Toast.makeText(activity,"No report found", Toast.LENGTH_LONG).show()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}