package com.example.dgtechhealthcare.pharmacist.presenter

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.utils.FirebasePresenter

class UploadImagePresenter(view : View) {

    val reference = FirebasePresenter(view)

    fun uploadImageToStorage(imageUri : Uri,activity: FragmentActivity) {
        val imgPath = reference.userProfileImgRef.child("${reference.currentUserId}.jpg")

        imgPath.putFile(imageUri).addOnSuccessListener {
            imgPath.downloadUrl.addOnSuccessListener {
                val downloadUri = it.toString()

                // save Image to Firebase Realtime Database
                reference.userReference.child(reference.currentUserId!!).child("profileImage").setValue(downloadUri)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(activity, "Image Uploaded", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}