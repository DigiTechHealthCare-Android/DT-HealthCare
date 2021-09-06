package com.example.dgtechhealthcare.signin

import android.content.Context

class SignInContract {

    interface View {
        fun showLoadingBar(context: Context)

        fun dismissLoadingBar(context: Context)

        fun signInFailure(context1: Context?)

        fun signInTest(details: Userdata?, context1: Context?)
    }
}