package com.example.dgtechhealthcare.contentManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtechhealthcare.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CRUDContentActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var addContent : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crudcontent)

        initializeValues()

        addContent.setOnClickListener {
            val frag = AddContentFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.contentFrame,frag).addToBackStack(null).commit()
        }
    }

    private fun initializeValues() {
        recyclerView = findViewById(R.id.contentRecyclerV)
        addContent = findViewById(R.id.addContentB)
    }
}