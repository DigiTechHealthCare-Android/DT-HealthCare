package com.example.dgtechhealthcare.userRegistration

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
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.signin.SignInActivity
import com.example.dgtechhealthcare.utils.FirebasePresenter

class NurseRegistrationFragment : Fragment() {

    lateinit var nameE : EditText
    lateinit var hospitalE : EditText
    lateinit var contactE : EditText
    lateinit var registerD : Button
    lateinit var dobE : EditText
    lateinit var gender : RadioGroup

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
        return inflater.inflate(R.layout.fragment_nurse_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        nameE = view.findViewById(R.id.fullNameN)
        hospitalE = view.findViewById(R.id.hospitalN)
        contactE = view.findViewById(R.id.contactNoN)
        registerD = view.findViewById(R.id.registerN)
        dobE = view.findViewById(R.id.dateOfBirth)
        gender =view.findViewById(R.id.Gender_RG)

        var genderP = ""
        gender.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.maleRadio) genderP = "Male"
            if(checkedId == R.id.femaleRadio) genderP = "Female"
        }

        registerD.setOnClickListener {
            val name = nameE.text
            val hospital = hospitalE.text
            val contact = contactE.text
            val dob = dobE.text

            if(name.isEmpty()) Toast.makeText(activity, R.string.name_empty, Toast.LENGTH_LONG).show()
            else if(hospital.isEmpty()) Toast.makeText(activity, R.string.hospital_empty,Toast.LENGTH_LONG).show()
            else if(contact.length > 10)  Toast.makeText(activity, R.string.invalid_mobile,Toast.LENGTH_LONG).show()
            else if (dob.isEmpty()) Toast.makeText(activity, R.string.dob_empty,Toast.LENGTH_LONG).show()
            else {
                val hm = HashMap<String,Any>()
                hm["username"] = name.toString()
                hm["email"] = reference.auth.currentUser?.email.toString()
                hm["hospital"] = hospital.toString()
                hm["contact"] = contact.toString()
                hm["dateOfBirth"] = dob.toString()
                hm["gender"] = genderP.toString()
                hm["accountType"] = "nurse"
                reference.userReference.child(reference.currentUserId!!).updateChildren(hm).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val nurseHashMap = HashMap<String, Any>()
                        nurseHashMap["nuid"] = reference.currentUserId.toString()
                        nurseHashMap["name"] = name.toString()
                        nurseHashMap["hospitalName"] = hospital.toString()

                        reference.nurseReference.child(reference.currentUserId!!).updateChildren(nurseHashMap).addOnCompleteListener {
                            if (it.isSuccessful){
                                Toast.makeText(activity, R.string.account_created,Toast.LENGTH_LONG).show()
                                val i = Intent(activity, SignInActivity::class.java)
                                startActivity(i)
                                activity?.finish()
                            }
                        }
                    }
                }
            }
        }
    }
}