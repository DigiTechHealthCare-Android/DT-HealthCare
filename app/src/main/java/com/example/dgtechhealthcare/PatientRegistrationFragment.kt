package com.example.dgtechhealthcare

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.example.dgtechhealthcare.utils.FirebasePresenter

class PatientRegistrationFragment : Fragment() {

    lateinit var nameE : EditText
    lateinit var dobE : EditText
    lateinit var contactE : EditText
    lateinit var gender : RadioGroup
    lateinit var registerB : Button

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
        return inflater.inflate(R.layout.fragment_patient_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        nameE = view.findViewById(R.id.fullNameP)
        dobE = view.findViewById(R.id.dobP)
        contactE = view.findViewById(R.id.contactP)
        gender =view.findViewById(R.id.Gender_RG)
        registerB = view.findViewById(R.id.registerP)

        var genderP = ""

        val name = nameE.text
        val dob = dobE.text
        val contact = contactE.text

        gender.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.maleRadio) genderP = "Male"
            if(checkedId == R.id.femaleRadio) genderP = "Female"
        }

        registerB.setOnClickListener {

            if(name.isEmpty()) Toast.makeText(activity,"Name is empty",Toast.LENGTH_LONG).show()
            else if(dob.isEmpty()) Toast.makeText(activity,"Date of birth is empty",Toast.LENGTH_LONG).show()
            else if (contact.isEmpty()) Toast.makeText(activity,"Contact No. is empty",Toast.LENGTH_LONG).show()
            else if (contact.length > 10 || contact.length < 10) Toast.makeText(activity,"Invalid mobile number",Toast.LENGTH_LONG).show()
            else {
                val hm = HashMap<String,Any>()
                hm["username"] = name.toString()
                hm["email"] = reference.auth.currentUser?.email.toString()
                hm["dateOfBirth"] = dob.toString()
                hm["gender"] = genderP.toString()
                hm["contactNo"] = contact.toString()
                hm["accountType"] = "patient"
                reference.userReference.child(reference.currentUserId!!).updateChildren(hm).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        Toast.makeText(activity,"Account successfully created",Toast.LENGTH_LONG).show()
                        val i = Intent(activity, SignInActivity::class.java)
                        startActivity(i)
                        activity?.finish()
                    }
                }
            }
        }
    }
}