package com.example.dgtechhealthcare.doctorPrescribeMedicine

import android.view.View
import androidx.fragment.app.FragmentActivity

class DoctorPrescribeMedicinePresenter(view : View) {

    val model = DoctorPrescribeMedicineModel(view)
    val view = DoctorPrescribeMedicineFragment()

    fun populateView(data: PrescrideMedicine, userType: String?) {
        model.populateModelView(data,userType)
    }

    fun sendRequest(choice: String, requireActivity: FragmentActivity) {
        model.sendRequestModel(choice,requireActivity)
    }

    fun prescribeMedicine(patientID: String?, userType: String?, requireActivity: FragmentActivity,
        data: PrescrideMedicine) {
        model.prescribeMedicineModel(patientID,userType,requireActivity,data)
    }

    fun nurseStatus(patientID: String?, data: PrescrideMedicine,activity: FragmentActivity) {
        model.nurseStatusModel(patientID,data,activity)
    }

    fun checkAccountType(userType: String?, data: PrescrideMedicine) {
        model.checkAccountModel(userType,data)
    }

    fun requestSentMessage(activity: FragmentActivity){
        view.requestSentMessage(activity)
    }

    fun statusUpdateMessage(activity: FragmentActivity){
        view.statusUpdateMessage(activity)
    }

    fun checkBoxMessage(activity: FragmentActivity){
        view.checkBoxMessage(activity)
    }
}