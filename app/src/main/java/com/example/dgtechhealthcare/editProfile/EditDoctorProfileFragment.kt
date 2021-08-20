package com.example.dgtechhealthcare.editProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter

class EditDoctorProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var editPresenter : EditProfilePresenter

    lateinit var editName : EditText
    lateinit var editPhone : EditText
    lateinit var editHospital : EditText
    lateinit var editSpecial : EditText
    lateinit var editUpdateB : Button

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

        initializeValues(view)

        val doctorDetails = DoctorClass(editName,editPhone,editHospital,editSpecial)
        editPresenter.populateEditDoctorProfile(doctorDetails)

        editUpdateB.setOnClickListener {
            editPresenter.updateDoctorProfile(doctorDetails)
        }
    }

    fun initializeValues(view : View){
        editName = view.findViewById(R.id.editDName)
        editPhone = view.findViewById(R.id.editDPhone)
        editHospital = view.findViewById(R.id.editDHospital)
        editSpecial = view.findViewById(R.id.editDSpecial)
        editUpdateB = view.findViewById(R.id.editDUpdateB)

    }
}