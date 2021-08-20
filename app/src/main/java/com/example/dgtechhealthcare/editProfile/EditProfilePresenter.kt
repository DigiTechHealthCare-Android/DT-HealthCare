package com.example.dgtechhealthcare.editProfile

import android.content.Context
import com.example.dgtechhealthcare.utils.FirebasePresenter

class EditProfilePresenter(val view: android.view.View) {

    val model : EditProfileModel = EditProfileModel(view)

    fun populateEditPatientProfile(patientDetails: PatientEditData){
        model.editPatientInfo(patientDetails)
    }

    fun updatePatientProfile(patientDetails: PatientEditData){
        model.updatePatientProfile(patientDetails)
    }
}