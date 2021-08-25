package com.example.dgtechhealthcare.view.fragments

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
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.editProfile.EditDoctorProfileFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class DoctorProfileFragment : Fragment() {

    lateinit var username : TextView
    lateinit var useremail : TextView
    lateinit var userhospital : TextView
    lateinit var userspecial : TextView
    lateinit var usercontact : TextView
    lateinit var profileIV : ImageView
    lateinit var editProfile : ImageView

    lateinit var reference : FirebasePresenter
    var galleryPick : Int = 0
    var imgUri : Uri = Uri.parse("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        username = view.findViewById(R.id.doctorName)
        useremail = view.findViewById(R.id.doctorEmail)
        userhospital = view.findViewById(R.id.doctorHospital)
        userspecial = view.findViewById(R.id.doctorSpecial)
        usercontact = view.findViewById(R.id.doctorContact)
        profileIV = view.findViewById(R.id.doctorIV)
        editProfile = view.findViewById(R.id.editDoctorProfile)

        populateDoctorProfile()

        profileIV.setOnClickListener {
            val gallery : Intent = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            startActivityForResult(gallery,galleryPick)
        }

        editProfile.setOnClickListener {
            editUserProfile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == Activity.RESULT_OK && data != null) {
                imgUri = data.data!!
                uploadToStorage(reference,reference.currentUserId,imgUri,requireActivity())
        } else Toast.makeText(activity,"ERROR!!", Toast.LENGTH_LONG).show()
    }

    private fun uploadToStorage(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri, requireActivity: FragmentActivity) {
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
            } else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
        }
        profileIV.setImageURI(imgUri)
    }

    private fun editUserProfile() {
        val frag = EditDoctorProfileFragment()
        activity?.supportFragmentManager
            ?.beginTransaction()?.replace(R.id.doctorProfileFrame,frag)
            ?.addToBackStack(null)?.commit()
    }

    private fun populateDoctorProfile() {

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")) {
                    val img = snapshot.child("profileImage").value.toString()
                    Picasso.get().load(img).into(profileIV)
                }

                username.text = snapshot.child("username").value.toString()
                useremail.text = snapshot.child("email").value.toString()
                userhospital.text = snapshot.child("hospital").value.toString()
                userspecial.text = snapshot.child("specialization").value.toString()
                usercontact.text = snapshot.child("contact").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}