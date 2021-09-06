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

class ContentManagerRegistrationFragment : Fragment() {

    lateinit var name : EditText
    lateinit var phone : EditText
    lateinit var location : EditText
    lateinit var registerCMB : Button

    lateinit var reference : FirebasePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_content_manager_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        registerCMB.setOnClickListener {
            if(name.text.isEmpty()) Toast.makeText(activity, R.string.name_empty,Toast.LENGTH_LONG).show()
            else if(phone.text.isEmpty()) Toast.makeText(activity, R.string.contact_empty,Toast.LENGTH_LONG).show()
            else if(location.text.isEmpty()) Toast.makeText(activity, R.string.location_empty,Toast.LENGTH_LONG).show()
            else {
                val hm = HashMap<String,Any>()
                hm["username"] = name.text.toString()
                hm["email"] = reference.auth.currentUser?.email.toString()
                hm["contact"] = phone.text.toString()
                hm["location"] = location.text.toString()
                hm["accountType"] = "contentManager"
                reference.userReference.child(reference.currentUserId!!).updateChildren(hm).addOnCompleteListener {
                    if(it.isSuccessful){
                        val cm = HashMap<String,Any>()
                        cm["cmuid"] = reference.currentUserId.toString()
                        cm["username"] = name.text.toString()
                        cm["location"] = location.text.toString()
                        reference.managerReference.child(reference.currentUserId!!).updateChildren(cm).addOnCompleteListener {
                            if(it.isSuccessful){
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

    private fun initializeValues(view: View) {
        name = view.findViewById(R.id.managerName)
        phone = view.findViewById(R.id.managerPhone)
        location = view.findViewById(R.id.managerLocation)
        registerCMB = view.findViewById(R.id.registerManager)
        reference = FirebasePresenter(view)
    }
}