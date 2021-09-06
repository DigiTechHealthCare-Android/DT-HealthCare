package com.example.dgtechhealthcare.contentManager.presenter

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.contentManager.ContentManagerProfileFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter

class FileStoragePresenter(view : View) {

    val reference = FirebasePresenter(view)
    val viewRef = ContentManagerProfileFragment()

    fun uploadProfilePictureToFirebase(f: String, uri: Uri, activity: Context){
        val path = reference.userProfileImgRef.child("${f}.pdf")
        path.putFile(uri).addOnCompleteListener {
            if(it.isSuccessful) {
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(reference.currentUserId!!).child("profileImage").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) viewRef.imageUploadMessage(activity)
                    }
                }
            }else viewRef.errorMessage(activity,it)
        }
    }

    fun uploadToStorage(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri, activity: FragmentActivity) {
        val resultUri = imgUri
        val path = reference.userProfileImgRef.child("$currentUserId.jpg")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful) {
                viewRef.profileImageMessage(activity)
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(currentUserId!!).child("profileImage").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) viewRef.imageUploadMessage(activity)
                    }
                }
            }
        }
    }

}