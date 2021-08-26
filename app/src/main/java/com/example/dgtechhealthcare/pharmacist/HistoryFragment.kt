package com.example.dgtechhealthcare.pharmacist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.pharmacist.model.RequestModel

class HistoryFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var requestModel : RequestModel
    lateinit var noRequest : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        recyclerView.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        recyclerView.layoutManager = layout
        requestModel.displayAllRequests(recyclerView,requireActivity(),"history",noRequest)

    }

    private fun initializeValues(view: View) {
        recyclerView = view.findViewById(R.id.historyRV)
        requestModel = RequestModel(view)
        noRequest = view.findViewById(R.id.noRecordsTV)
    }
}