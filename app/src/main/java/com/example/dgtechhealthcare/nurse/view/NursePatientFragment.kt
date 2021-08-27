package com.example.dgtechhealthcare.nurse.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.nurse.presenter.AllPatientsInfoPresenter

class NursePatientFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var patientInfoPresenter : AllPatientsInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nurse_patients, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        recyclerView.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        recyclerView.layoutManager = layout

        patientInfoPresenter.displayNursePatients(recyclerView,requireActivity(),layout)
    }

    private fun initializeValues(view: View) {
        recyclerView = view.findViewById(R.id.nursePatientRV)
        patientInfoPresenter = AllPatientsInfoPresenter(view)
    }
}