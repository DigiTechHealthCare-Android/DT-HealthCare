package com.example.dgtechhealthcare.splashActivity

import android.content.Context

class SplashContract {

    interface View {

        fun welcomeTextDoctor(context: Context)

        fun welcomeTextNurse(context:Context)

        fun welcomeTextPharmacist(context: Context)

        fun welcomeTextContentManager(context: Context)
    }
}