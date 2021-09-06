package com.example.dgtechhealthcare.contentManager.contract

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask

class CMProfileContract {

    interface View{

        fun imageUploadMessage(context: Context)

        fun errorMessage(context: Context, task: Task<UploadTask.TaskSnapshot>)

        fun profileImageMessage(context: Context)
    }
}