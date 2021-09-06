package com.example.dgtechhealthcare.doctorPrescribeMedicine

import android.content.Context

class DoctorPrescribeMedicineContract {

    interface View{

        fun requestSentMessage(context: Context)

        fun statusUpdateMessage(context: Context)

        fun checkBoxMessage(context: Context)
    }

}