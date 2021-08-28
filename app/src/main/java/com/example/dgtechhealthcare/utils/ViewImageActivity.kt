package com.example.dgtechhealthcare.utils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.dgtechhealthcare.R

class ViewImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        val i = intent.getStringExtra("url")

        val reportImage = findViewById<ImageView>(R.id.viewImageReport)
        Glide.with(this).load(i).into(reportImage)
    }
}