package com.example.dgtechhealthcare

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.view.NurseNavigationActivity

class NurseRegistrationFragment : Fragment() {

    lateinit var nameE : EditText
    lateinit var hospitalE : EditText
    lateinit var contactE : EditText
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
        return inflater.inflate(R.layout.fragment_nurse_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        nameE = view.findViewById(R.id.fullNameN)
        hospitalE = view.findViewById(R.id.hospitalN)
        contactE = view.findViewById(R.id.contactNoN)
        registerD = view.findViewById(R.id.registerN)

        registerD.setOnClickListener {
            val name = nameE.text
            val hospital = hospitalE.text
            val contact = contactE.text

            if(name.isEmpty()) Toast.makeText(activity,"Name is empty", Toast.LENGTH_LONG).show()
            else if(hospital.isEmpty()) Toast.makeText(activity,"Hospital name is empty",Toast.LENGTH_LONG).show()
            else if(contact.length > 10)  Toast.makeText(activity,"Invalid mobile number",Toast.LENGTH_LONG).show()
            else {
                val hm = HashMap<String,Any>()
                hm["username"] = name.toString()
                hm["email"] = reference.auth.currentUser?.email.toString()
                hm["hospital"] = hospital.toString()
                hm["contact"] = contact.toString()
                hm["accountType"] = "nurse"
                reference.userReference.child(reference.currentUserId!!).updateChildren(hm).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        Toast.makeText(activity,"Account successfully created",Toast.LENGTH_LONG).show()
                        val i = Intent(activity,NurseNavigationActivity::class.java)
                        startActivity(i)
                        activity?.finish()
                    } else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}