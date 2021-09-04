package com.example.dgtechhealthcare.patient.presenter

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.utils.FirebasePresenter

class PatientUploadClass(view : View) {

    val reference = FirebasePresenter(view)

    fun uploadHistoryToStorage(reportUri: Uri, requireActivity: FragmentActivity) {
        val resultUri = reportUri
        val path = reference.oldReportRef.child("${reference.currentUserId}.pdf")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(requireActivity,"Report Uploaded", Toast.LENGTH_SHORT).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(reference.currentUserId!!).child("medicalHistory").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(requireActivity,"Report Uploaded", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun uploadToStorage(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri, activity: Context) {

        val resultUri = imgUri
        val path = reference.userProfileImgRef.child("$currentUserId.jpg")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(activity,"Profile image changed",Toast.LENGTH_SHORT).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(currentUserId!!).child("profileImage").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,"Image stored",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun uploadReportToFirebase(f: String,uri : Uri,activity: Context){
        val path = reference.userReportRef.child("${f}.pdf")
        path.putFile(uri).addOnCompleteListener {
            if(it.isSuccessful) {
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(reference.currentUserId!!).child("report").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,"Report Uploaded",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun uploadProfilePictureToFirebase(f: String,uri: Uri,activity: Context){
        val path = reference.userProfileImgRef.child("${f}.pdf")
        path.putFile(uri).addOnCompleteListener {
            if(it.isSuccessful) {
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(reference.currentUserId!!).child("profileImage").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,"Image Uploaded",Toast.LENGTH_SHORT).show()
                    }
                }
            }else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
        }
    }

    fun uploadMedicalReport(reportUri : Uri,activity: Context) {
        val resultUri = reportUri
        val path = reference.userReportRef.child("${reference.currentUserId}.pdf")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(activity,"Report Uploaded",Toast.LENGTH_SHORT).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(reference.currentUserId!!).child("report").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,"Report Uploaded",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}