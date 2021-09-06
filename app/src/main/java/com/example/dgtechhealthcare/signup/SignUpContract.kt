package com.example.dgtechhealthcare.signup

import android.content.Context

class SignUpContract {

    interface View {

        fun emptyNameMessage(context: Context)

        fun emptyEmailMessage(context: Context)

        fun emptyPasswordMessage(context: Context)
    }
}