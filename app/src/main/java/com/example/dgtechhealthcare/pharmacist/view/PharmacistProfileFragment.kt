package com.example.dgtechhealthcare.pharmacist.view

import android.app.Activity
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
import com.example.dgtechhealthcare.pharmacist.model.PharmacistData
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class PharmacistProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter

    lateinit var pharmacistProfileImg : ImageView
    lateinit var nameTextView : TextView
    lateinit var pharmacyNameTextView: TextView
    lateinit var mobileTextView : TextView
    lateinit var locationTextView : TextView
    lateinit var editButtonView : ImageView

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
        return inflater.inflate(R.layout.fragment_pharmacist_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        pharmacistProfileImg = view.findViewById(R.id.pharmacistIV)
        nameTextView = view.findViewById(R.id.usernameTV)
        pharmacyNameTextView = view.findViewById(R.id.pharmacyNameTV)
        mobileTextView = view.findViewById(R.id.contactTV)
        locationTextView = view.findViewById(R.id.locationTV)
        editButtonView = view.findViewById(R.id.editBV)

        editButtonView.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val frag = EditPharmacistFragment()
            transaction?.replace(R.id.fragment_container_pharmacist, frag)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        pharmacistProfileImg.setOnClickListener {
            val image = Intent().setAction(Intent.ACTION_GET_CONTENT)
            image.setType("image/*")
            startActivityForResult(image, imagePick)
        }


        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val pharmacist = snapshot.getValue(PharmacistData::class.java)

                if (pharmacist?.profileImage == null){
                    pharmacistProfileImg.setImageResource(R.drawable.profile)
                }
                else{
                    Picasso.get().load(pharmacist?.profileImage).into(pharmacistProfileImg)
                }

                nameTextView.text = pharmacist?.username
                pharmacyNameTextView.text = pharmacist?.pharmacyName
                mobileTextView.text = "${pharmacist?.contact}"
                locationTextView.text = "${pharmacist?.location}"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imagePick && resultCode == Activity.RESULT_OK && data != null){
            imageUri = data.data!!
            uploadImageToStorage()
            pharmacistProfileImg.setImageURI(imageUri)
        }
    }

    private fun uploadImageToStorage() {
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