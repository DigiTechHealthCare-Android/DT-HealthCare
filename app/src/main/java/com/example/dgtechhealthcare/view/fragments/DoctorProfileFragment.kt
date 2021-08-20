package com.example.dgtechhealthcare.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.dgtechhealthcare.R
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

        editProfile.setOnClickListener {
            editUserProfile()
        }
    }

    private fun editUserProfile() {
        TODO("Not yet implemented")
    }

    private fun populateDoctorProfile() {

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")) {
                    val img = snapshot.child("profileImage").value.toString()
                    Picasso.get().load(img).into(profileIV)
                }

                username.text = snapshot.child("username").value.toString()
                useremail.text = "Email: " + snapshot.child("email").value.toString()
                userhospital.text = "Hospital: " + snapshot.child("hospital").value.toString()
                userspecial.text = "Specialization: " + snapshot.child("specialization").value.toString()
                usercontact.text = "Contact number: " + snapshot.child("contact").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}