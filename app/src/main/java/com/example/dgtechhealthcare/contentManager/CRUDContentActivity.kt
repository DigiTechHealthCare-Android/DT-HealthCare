package com.example.dgtechhealthcare.contentManager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtechhealthcare.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CRUDContentActivity : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var addContent : FloatingActionButton

    lateinit var showContent : ContentMangerShowContentFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_crudcontent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        addContent.setOnClickListener {
            val frag = AddContentFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.contentFrame,frag)?.addToBackStack(null)?.commit()
        }

        recyclerView.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        recyclerView.layoutManager = layout

        showContent.displayContent(recyclerView,requireActivity(),"contentManager")
    }

    private fun initializeValues(view: View) {
        recyclerView = view.findViewById(R.id.contentRecyclerV)
        addContent = view.findViewById(R.id.addContentB)
        showContent = ContentMangerShowContentFragment(View(activity))
    }
}