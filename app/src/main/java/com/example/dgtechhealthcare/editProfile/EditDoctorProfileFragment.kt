package com.example.dgtechhealthcare.editProfile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import kotlinx.android.synthetic.main.fragment_edit_doctor_profile.*

class EditDoctorProfileFragment : Fragment(), EditProfileContract.Doctor {

    lateinit var reference : FirebasePresenter
    lateinit var editPresenter : EditProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_doctor_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)
        editPresenter = EditProfilePresenter(view)

        val doctorDetails = DoctorClass(editDName,editDPhone,editDHospital,editDSpecial)
        editPresenter.populateEditDoctorProfile(doctorDetails)

        editDUpdateB.setOnClickListener {
            editPresenter.updateDoctorProfile(doctorDetails)
        }
    }

    override fun profileUpdated(context: Context) {
        Toast.makeText(context,R.string.profile_updated, Toast.LENGTH_LONG).show()
    }
}