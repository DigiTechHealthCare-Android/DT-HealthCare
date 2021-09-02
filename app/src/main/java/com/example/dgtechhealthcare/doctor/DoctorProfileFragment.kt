package com.example.dgtechhealthcare.doctor

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
import com.example.dgtechhealthcare.editProfile.EditDoctorProfileFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter

class DoctorProfileFragment : Fragment() {

    lateinit var username : TextView
    lateinit var useremail : TextView
    lateinit var userhospital : TextView
    lateinit var userspecial : TextView
    lateinit var usercontact : TextView
    lateinit var profileIV : ImageView
    lateinit var editProfile : ImageView
    lateinit var cameraEdit : ImageView

    lateinit var reference : FirebasePresenter
    lateinit var presenter : DoctorProfilePresenter
    var galleryPick : Int = 0
    var imgUri : Uri = Uri.parse("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        val data = DoctorProfileData(username, useremail, userhospital, userspecial, usercontact, profileIV)

        presenter.populateProfile(data)

        cameraEdit.setOnClickListener {
            val gallery : Intent = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            startActivityForResult(gallery,galleryPick)
        }

        editProfile.setOnClickListener {
            editUserProfile()
        }
    }

    private fun initializeValues(view: View) {
        reference = FirebasePresenter(view)
        presenter = DoctorProfilePresenter(view)

        username = view.findViewById(R.id.doctorName)
        useremail = view.findViewById(R.id.doctorEmail)
        userhospital = view.findViewById(R.id.doctorHospital)
        userspecial = view.findViewById(R.id.doctorSpecial)
        usercontact = view.findViewById(R.id.doctorContact)
        profileIV = view.findViewById(R.id.doctorIV)
        editProfile = view.findViewById(R.id.editDoctorProfile)
        cameraEdit = view.findViewById(R.id.doctorCameraEdit)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.data!!
            presenter.uploadProfilePicture(reference,reference.currentUserId,imgUri,requireActivity())
            profileIV.setImageURI(imgUri)
        } else Toast.makeText(activity,"ERROR!!", Toast.LENGTH_LONG).show()
    }

    private fun editUserProfile() {
        val frag = EditDoctorProfileFragment()
        activity?.supportFragmentManager
            ?.beginTransaction()?.replace(R.id.doctorProfileFrame,frag)
            ?.addToBackStack(null)?.commit()
    }
}