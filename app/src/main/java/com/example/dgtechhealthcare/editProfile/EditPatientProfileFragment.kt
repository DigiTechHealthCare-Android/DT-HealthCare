package com.example.dgtechhealthcare.editProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter

class EditPatientProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var editPresenter : EditProfilePresenter

    lateinit var editName : EditText
    lateinit var editPhone : EditText
    lateinit var editDob : EditText
    lateinit var editRG : RadioGroup
    lateinit var editFather : EditText
    lateinit var editMother : EditText
    lateinit var editOther : EditText
    lateinit var editDoctor : EditText
    lateinit var editHospital : EditText
    lateinit var editUpdate : Button

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

        initializeValues(view)

        val patientDetails = PatientClass(editName,editPhone,editDob,editRG,editFather,editMother,editOther,editDoctor,editHospital)
        editPresenter.populateEditPatientProfile(patientDetails)

        editUpdate.setOnClickListener {

            /*editRG.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId){
                    R.id.editPMale -> gender = "Male"
                    R.id.editPFemale -> gender = "Female"
                    R.id.editPOther -> gender = "Other"
                }
                Toast.makeText(activity,"$gender",Toast.LENGTH_SHORT).show()
            }*/
            editPresenter.updatePatientProfile(patientDetails)
        }
    }

    fun initializeValues(view : View){
        editName = view.findViewById(R.id.editPName)
        editPhone = view.findViewById(R.id.editPPhone)
        editDob = view.findViewById(R.id.editPDob)
        editRG = view.findViewById(R.id.editNurseGender)
        editFather = view.findViewById(R.id.editPFatherName)
        editMother = view.findViewById(R.id.editPMotherName)
        editOther = view.findViewById(R.id.editPFamilyInfo)
        editDoctor = view.findViewById(R.id.editPDoctorName)
        editHospital = view.findViewById(R.id.editPHospitalName)
        editUpdate = view.findViewById(R.id.editPUpdateB)
    }
}