package com.example.dgtechhealthcare.patientInfo

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PatientInfoPresenter(val view: View) {

    val model = PatientInfoModel(view)

    fun displayAllPatients(patientList:RecyclerView,activity:FragmentActivity,layoutManager: LinearLayoutManager){
        model.displayAllPatients(patientList,activity,layoutManager)
    }

    fun displayNursePatients(patientList: RecyclerView, activity: FragmentActivity, layoutManager: LinearLayoutManager){
        model.displayNursePatients(patientList, activity, layoutManager)
    }
}