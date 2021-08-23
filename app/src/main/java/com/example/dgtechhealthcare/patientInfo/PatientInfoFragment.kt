package com.example.dgtechhealthcare.patientInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtechhealthcare.R

class PatientInfoFragment : Fragment() {

    lateinit var patientRecycleView : RecyclerView
    lateinit var patientInfoPresenter : PatientInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        patientRecycleView.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        patientRecycleView.layoutManager = layout

        patientInfoPresenter.displayAllPatients(patientRecycleView,requireActivity(),layout)
    }

    fun initializeValues(view: View){
        patientRecycleView = view.findViewById(R.id.doctorPatientR)
        patientInfoPresenter = PatientInfoPresenter(view)
    }
}