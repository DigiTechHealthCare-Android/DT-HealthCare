package com.example.dgtechhealthcare.editProfile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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
    lateinit var editImage : ImageView
    lateinit var editUpload : Button

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

        initializeValues(view)

        val patientDetails = PatientClass(editName,editPhone,editDob,editRG,editFather,editMother,
            editOther,editDoctor,editHospital,editImage,editUpload)
        editPresenter.populateEditPatientProfile(patientDetails)

        editUpdate.setOnClickListener {
            editPresenter.updatePatientProfile(patientDetails)
        }

        editUpload.setOnClickListener {
            val gallery : Intent = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            startActivityForResult(gallery,1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.data!!
            uploadToStorage(reference,reference.currentUserId,imgUri,requireActivity())
            editImage.setImageURI(imgUri)
        } else Toast.makeText(activity,"ERROR!!",Toast.LENGTH_LONG).show()
    }

    fun uploadToStorage(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri, activity: Context) {

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
    }

    fun initializeValues(view : View){
        editName = view.findViewById(R.id.editPName)
        editPhone = view.findViewById(R.id.editPPhone)
        editDob = view.findViewById(R.id.editPDob)
        editRG = view.findViewById(R.id.editNurseGender)
        editFather = view.findViewById(R.id.editPFather)
        editMother = view.findViewById(R.id.editPMother)
        editOther = view.findViewById(R.id.editPFamily)
        editDoctor = view.findViewById(R.id.editPDoctor)
        editHospital = view.findViewById(R.id.editPHospital)
        editUpdate = view.findViewById(R.id.editPUpdate)
        editImage = view.findViewById(R.id.editProfileImageP)
        editUpload = view.findViewById(R.id.editUploadImageP)
    }
}