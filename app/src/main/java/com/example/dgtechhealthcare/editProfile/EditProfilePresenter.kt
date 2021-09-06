package com.example.dgtechhealthcare.editProfile

import androidx.fragment.app.FragmentActivity

class EditProfilePresenter(val view: android.view.View) {

    val model : EditProfileModel = EditProfileModel(view)

    fun populateEditPatientProfile(patientDetails: PatientClass){
        model.editPatientInfo(patientDetails)
    }

    fun updatePatientProfile(patientDetails: PatientClass){
        model.updatePatientProfile(patientDetails)
    }

    fun populateEditDoctorProfile(doctorDetails: DoctorClass) {
        model.editDoctorInfo(doctorDetails)
    }

    fun updateDoctorProfile(doctorDetails: DoctorClass){
        model.updateDoctorProfile(doctorDetails)
    }

    fun populateEditContentManagerProfile(pharmaDetails:ManagerClass){
        model.editContentManagerInfo(pharmaDetails)
    }

    fun updateContentManagerProfile(managerDetails:ManagerClass){
        model.updateManagerProfile(managerDetails)
    }

    fun doctorUpdateMessage(activity: FragmentActivity){
        EditDoctorProfileFragment().profileUpdated(activity)
    }

    fun patientUpdateMessage(activity: FragmentActivity){
        EditPatientProfileFragment().profileUpdated(activity)
    }

    fun managerUpdateMessage(activity: FragmentActivity){
        EditContentManagerProfileFragment().profileUpdated(activity)
    }

    fun patientUpdateDoctorMessage(activity: FragmentActivity){
        EditPatientProfileFragment().doctorUpdated(activity)
    }
}