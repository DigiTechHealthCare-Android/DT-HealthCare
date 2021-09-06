package com.example.dgtechhealthcare.editProfile

import android.content.Context

class EditProfileContract {

    interface Patient{
        fun doctorUpdated(context: Context)

        fun profileUpdated(context: Context)
    }

    interface Doctor{
        fun profileUpdated(context: Context)
    }

    interface Manager{
        fun profileUpdated(context: Context)
    }
}