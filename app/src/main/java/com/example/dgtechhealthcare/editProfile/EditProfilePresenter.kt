package com.example.dgtechhealthcare.editProfile

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
}