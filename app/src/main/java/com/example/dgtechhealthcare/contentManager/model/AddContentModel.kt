package com.example.dgtechhealthcare.contentManager.model

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.contentManager.AddContentFragment
import com.example.dgtechhealthcare.contentManager.presenter.AddContentPresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.HashMap

class AddContentModel(view : View) {

    val reference = FirebasePresenter(view)

    fun publishArticle(title: String, desc: String, url: String, type: String, contentUid: String,
        requireActivity: FragmentActivity,pdfUpload : Boolean) {

        val presenterRef = AddContentPresenter(View(requireActivity))
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val img = snapshot.child("profileImage").value.toString()
                val username = snapshot.child("username").value.toString()

                val hm = HashMap<String,Any>()
                hm["publisherName"] = username
                hm["publisherImage"] = img
                hm["title"] = title
                hm["desc"] = desc
                hm["url"] = url
                hm["time"] = Calendar.getInstance().time.toString()
                hm["type"] = type
                hm["views"] = 0
                if(type.compareTo("video")==0 || type.compareTo("research")==0){
                    if (url.isNullOrEmpty() && type.compareTo("video")==0){
                        Toast.makeText(requireActivity, R.string.url_required, Toast.LENGTH_LONG).show()
                    }else {
                        reference.articleReference.child(contentUid.toString()).updateChildren(hm)
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    val h = HashMap<String,Any>()
                                    h["contentUid"] = contentUid
                                    h["time"] = Calendar.getInstance().time.toString()
                                    reference.managerReference.child(reference.currentUserId!!).child("articles").child(contentUid).updateChildren(h).addOnCompleteListener {
                                        presenterRef.publishContentMessage(requireActivity)
                                        requireActivity?.supportFragmentManager?.popBackStack()
                                    }
                                }
                            }
                    }
                }
                if (type.compareTo("image")==0){
                    reference.articleReference.child(contentUid.toString()).updateChildren(hm)
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                val h = HashMap<String,Any>()
                                h["contentUid"] = contentUid
                                h["time"] = Calendar.getInstance().time.toString()
                                reference.managerReference.child(reference.currentUserId!!).child("articles").child(contentUid).updateChildren(h).addOnCompleteListener {
                                    presenterRef.publishContentMessage(requireActivity)
                                    requireActivity?.supportFragmentManager?.popBackStack()
                                }
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun uploadImageToDatabase(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri,
        requireActivity: FragmentActivity, contentUid: String) {
        val presenterRef = AddContentPresenter(View(requireActivity))
        val resultUri = imgUri
        val time = Calendar.getInstance().time.toString()
        val path = reference.contentPostRef.child(currentUserId + time + ".jpg")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful){
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.articleReference.child(contentUid).child("imageRef").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) presenterRef.imageUploadMessage(requireActivity)
                    }
                }
            }
        }
    }

    fun uploadReport(currentUserId: String?, imgUri: Uri, requireActivity: FragmentActivity, contentUid: String) {
        val presenterRef = AddContentPresenter(View(requireActivity))
        val resultUri = imgUri
        val time = Calendar.getInstance().time.toString()
        val path = reference.contentPostRef.child(currentUserId + time + "RESEARCH")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful){
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.articleReference.child(contentUid).child("researchRef").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) presenterRef.imageUploadMessage(requireActivity)
                    }
                }
            }
        }
    }
}