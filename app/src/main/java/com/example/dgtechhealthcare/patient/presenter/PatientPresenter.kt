package com.example.dgtechhealthcare.patient.presenter

import android.view.View
import com.example.dgtechhealthcare.patient.model.PatientAdditionalModel
import com.example.dgtechhealthcare.patient.model.PatientDataClass

class PatientPresenter(view: View) {

    val model = PatientAdditionalModel(view)

    fun populateAdditionalInfo(userType: String, data: PatientDataClass){
        model.populateView(userType,data)
    }
}