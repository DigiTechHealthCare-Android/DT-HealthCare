package com.example.dgtechhealthcare.patient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.contentManager.ContentMangerShowContentFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter

class PatientArticleFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeLayout : SwipeRefreshLayout

    lateinit var reference : FirebasePresenter
    lateinit var showContent : ContentMangerShowContentFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        val layout = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        recyclerView.layoutManager = layout

        showContent.displayContent(recyclerView,requireActivity(),"patient")

        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = false
        }
    }

    private fun initializeValues(view: View) {
        recyclerView = view.findViewById(R.id.patientArticleRV)
        reference = FirebasePresenter(view)
        swipeLayout = view.findViewById(R.id.swipeLayout)
        showContent = ContentMangerShowContentFragment(view)
    }
}