package com.example.dgtechhealthcare.utils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.dgtechhealthcare.R
import com.squareup.picasso.Picasso

class ViewImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        val i = intent.getStringExtra("url")

        val reportImage = findViewById<ImageView>(R.id.viewImageReport)
        Picasso.get().load(i).into(reportImage)
    }
}