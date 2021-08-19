package com.example.dgtechhealthcare.utils

import android.app.ProgressDialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dgtechhealthcare.R
import com.github.barteksc.pdfviewer.PDFView
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ViewPdfActivity : AppCompatActivity() {

    lateinit var progressDialog : ProgressDialog
    lateinit var pdfView : PDFView
    lateinit var urls : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pdf)

        pdfView = findViewById(R.id.viewPDF)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        urls = intent.getStringExtra("url").toString()
        getPdf().execute(urls)
    }

    inner class getPdf : AsyncTask<String, Void, InputStream>() {
        override fun doInBackground(vararg params: String?): InputStream? {
            var inputStream : InputStream? = null
            try{
                val url = URL(params[0])
                val urlConnection : HttpURLConnection = url.openConnection() as HttpURLConnection

                if(urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            } catch (e : IOException) {
                return null
            }
            return inputStream
        }

        override fun onPostExecute(result: InputStream?) {
            super.onPostExecute(result)
            pdfView.fromStream(result).load()
            progressDialog.dismiss()
        }
    }
}