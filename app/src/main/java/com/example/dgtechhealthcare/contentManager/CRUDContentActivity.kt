package com.example.dgtechhealthcare.contentManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtechhealthcare.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CRUDContentActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var addContent : FloatingActionButton

    lateinit var showContent : ContentMangerShowContentFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crudcontent)

        initializeValues()

        addContent.setOnClickListener {
            val frag = AddContentFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.contentFrame,frag).addToBackStack(null).commit()
        }

        recyclerView.setHasFixedSize(true)
        val layout = LinearLayoutManager(this)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        recyclerView.layoutManager = layout

        showContent.displayContent(recyclerView,this)
    }

    private fun initializeValues() {
        recyclerView = findViewById(R.id.contentRecyclerV)
        addContent = findViewById(R.id.addContentB)
        showContent = ContentMangerShowContentFragment(View(this))
    }
}