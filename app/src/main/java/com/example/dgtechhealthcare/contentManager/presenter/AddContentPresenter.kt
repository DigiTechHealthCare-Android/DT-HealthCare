package com.example.dgtechhealthcare.contentManager.presenter

import android.net.Uri
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.contentManager.model.AddContentModel
import com.example.dgtechhealthcare.utils.FirebasePresenter

class AddContentPresenter(view : View) {

    val model = AddContentModel(view)

    fun publishArticle(title: String, desc: String, url: String, type: String, contentUid: String,
        requireActivity: FragmentActivity,uploadPdf:Boolean) {
        model.publishArticle(title,desc,url,type,contentUid,requireActivity,uploadPdf)
    }

    fun uploadImage(reference: FirebasePresenter, currentUserId: String, imgUri: Uri,
        activity: FragmentActivity, contentUid: String){
        model.uploadImageToDatabase(reference,currentUserId,imgUri,activity,contentUid)
    }

}