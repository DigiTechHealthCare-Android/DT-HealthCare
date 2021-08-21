package com.example.dgtechhealthcare.nurse.view

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.nurse.model.ProfileData
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class NurseProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter

    lateinit var nurseProfileImg : ImageView
    lateinit var nameTextView : TextView
    lateinit var mobileTextView : TextView
    lateinit var hospitalNameTextView : TextView

    private val imagePick = 0
    lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nurse_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        nurseProfileImg = view.findViewById(R.id.nurseIV)
        nameTextView = view.findViewById(R.id.nurseNameTV)
        mobileTextView = view.findViewById(R.id.nurseContactTV)
        hospitalNameTextView = view.findViewById(R.id.hospitalNameTV)

        nurseProfileImg.setOnClickListener {
            val image = Intent().setAction(Intent.ACTION_GET_CONTENT)
            image.setType("image/*")
            startActivityForResult(image, imagePick)
        }

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nurserData = snapshot.getValue(ProfileData::class.java)

                Picasso.get().load(nurserData?.profileImage).into(nurseProfileImg)
                nameTextView.text = nurserData?.username
                mobileTextView.text = "Contact Number: ${nurserData?.contact}"
                hospitalNameTextView.text = "Hospital Name: ${nurserData?.hospital}"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imagePick && resultCode == RESULT_OK && data != null){
            imageUri = data.data!!
            uploadImageToStrorage()
            nurseProfileImg.setImageURI(imageUri)
        }
    }

    private fun uploadImageToStrorage() {
        val imgPath = reference.userProfileImgRef.child("${reference.currentUserId}.jpg")

        imgPath.putFile(imageUri).addOnSuccessListener {
            imgPath.downloadUrl.addOnSuccessListener {
                val downloadUri = it.toString()

                // save Image to Firebase Realtime Database
                reference.userReference.child(reference.currentUserId!!).child("profileImage").setValue(downloadUri)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this.activity, "Image Uploaded", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
}