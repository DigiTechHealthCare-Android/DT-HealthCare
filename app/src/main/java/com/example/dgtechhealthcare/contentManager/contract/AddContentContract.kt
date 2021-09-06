package com.example.dgtechhealthcare.contentManager.contract

import android.content.Context

class AddContentContract {

    interface View{

        fun publishContentMessage(context: Context)

        fun imageUploadMessage(context: Context)
    }

}