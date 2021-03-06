package com.example.dgtechhealthcare.userRegistration

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.signin.SignInActivity
import com.example.dgtechhealthcare.utils.FirebasePresenter

class DoctorRegistrationFragment : Fragment() {
    lateinit var nameD : EditText
    lateinit var hospitalD : EditText
    lateinit var specializationD : EditText
    lateinit var contactD : EditText
    lateinit var registerD : Button

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
        return inflater.inflate(R.layout.fragment_doctor_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        nameD = view.findViewById(R.id.fullNameD)
        hospitalD = view.findViewById(R.id.hospitalNameD)
        specializationD = view.findViewById(R.id.specializationD)
        contactD = view.findViewById(R.id.contactNoD)
        registerD = view.findViewById(R.id.registerD)

        registerD.setOnClickListener {
            val name = nameD.text
            val hospital = hospitalD.text
            val specialization = specializationD.text
            val contact = contactD.text
            if(name.isEmpty()) Toast.makeText(activity, R.string.name_empty,Toast.LENGTH_LONG).show()
            else if(hospital.isEmpty()) Toast.makeText(activity, R.string.hospital_empty,Toast.LENGTH_LONG).show()
            else if(specialization.isEmpty()) Toast.makeText(activity, R.string.special_empty,Toast.LENGTH_LONG).show()
            else if(contact.length > 10) Toast.makeText(activity, R.string.invalid_mobile,Toast.LENGTH_LONG).show()
            else {
                val hm = HashMap<String,Any>()
                hm["username"] = name.toString()
                hm["email"] = reference.auth.currentUser?.email.toString()
                hm["hospital"] = hospital.toString()
                hm["specialization"] = specialization.toString()
                hm["contact"] = contact.toString()
                hm["accountType"] = "doctor"
                reference.userReference.child(reference.currentUserId!!).updateChildren(hm).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val dm = HashMap<String,Any>()
                        dm["duid"] = reference.currentUserId.toString()
                        dm["username"] = name.toString()
                        dm["hospital"] = hospital.toString()
                        reference.doctorReference.child(reference.currentUserId!!).updateChildren(dm).addOnCompleteListener {
                            if(it.isSuccessful) {
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