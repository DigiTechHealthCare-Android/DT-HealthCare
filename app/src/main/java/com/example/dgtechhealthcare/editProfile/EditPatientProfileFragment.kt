package com.example.dgtechhealthcare.editProfile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import kotlinx.android.synthetic.main.fragment_edit_patient_profile.*

class EditPatientProfileFragment : Fragment(), EditProfileContract.Patient {

    lateinit var reference : FirebasePresenter
    lateinit var editPresenter : EditProfilePresenter

    var imgUri : Uri = Uri.parse("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_patient_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)
        editPresenter = EditProfilePresenter(view)

        val patientDetails = PatientClass(editPName,editPPhone,editPDob,editNurseGender,editPFather,editPMother,
            editPFamily,editPDoctor,editPHospital)
        editPresenter.populateEditPatientProfile(patientDetails)

        editPUpdate.setOnClickListener {
            editPresenter.updatePatientProfile(patientDetails)
        }
    }

    override fun doctorUpdated(context: Context) {
        Toast.makeText(context,"Doctor registered",Toast.LENGTH_SHORT).show()
    }

    override fun profileUpdated(context: Context) {
        Toast.makeText(context,R.string.profile_updated,Toast.LENGTH_SHORT).show()
    }
}