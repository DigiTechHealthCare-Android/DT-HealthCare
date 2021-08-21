package com.example.dgtechhealthcare.patientInfo

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class PatientInfoPresenter(val view: View) {

    val model = PatientInfoModel(view)

    fun displayAllPatients(patientList:RecyclerView,activity:FragmentActivity){
        model.displayAllPatients(patientList,activity)
    }
}