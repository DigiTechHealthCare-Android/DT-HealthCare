package com.example.dgtechhealthcare.nurse.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AllPatientsInfoPresenter(val view: View) {

    val model = AllPatientsInfoModel(view)

    fun displayNursePatients(patientList: RecyclerView, activity: FragmentActivity, layoutManager: LinearLayoutManager){
        model.displayNursePatients(patientList, activity, layoutManager)
    }
}