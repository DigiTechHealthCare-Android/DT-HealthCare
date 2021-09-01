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
import com.example.dgtechhealthcare.view.PharmacistDrawerNavigationActivity

class PharmacistRegistrationFragment : Fragment() {

    lateinit var nameE : EditText
    lateinit var pharmacyName: EditText
    lateinit var locationE : EditText
    lateinit var contactE : EditText
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
        return inflater.inflate(R.layout.fragment_pharmacist_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        nameE = view.findViewById(R.id.userPharmacistName)
        pharmacyName = view.findViewById(R.id.pharmacyName)
        locationE = view.findViewById(R.id.locationPharmacy)
        contactE = view.findViewById(R.id.contactPharmacy)
        registerB = view.findViewById(R.id.registerPharmacy)

        registerB.setOnClickListener {
            val name = nameE.text
            val pharmacyName = pharmacyName.text
            val location = locationE.text
            val contact = contactE.text

            if(name.isEmpty()) Toast.makeText(activity,"Name is empty",Toast.LENGTH_LONG).show()
            else if (pharmacyName.isEmpty()) Toast.makeText(activity, "Pharmacy Name is empty", Toast.LENGTH_LONG).show()
            else if(location.isEmpty()) Toast.makeText(activity,"Location is empty",Toast.LENGTH_LONG).show()
            else if(contact.length > 10) Toast.makeText(activity,"Invalid mobile number",Toast.LENGTH_LONG).show()
            else {
                val hm = HashMap<String,Any>()
                hm["username"] = name.toString()
                hm["pharmacyName"] = pharmacyName.toString()
                hm["email"] = reference.auth.currentUser?.email.toString()
                hm["location"] = location.toString()
                hm["contact"] = contact.toString()
                hm["accountType"] = "pharmacist"
                reference.userReference.child(reference.currentUserId!!).updateChildren(hm).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val hm = HashMap<String,Any>()
                        hm["phuid"] = reference.currentUserId.toString()
                        hm["username"] = pharmacyName.toString()
                        hm["location"] = location.toString()
                        reference.pharmaReference.child(reference.currentUserId!!).updateChildren(hm).addOnCompleteListener {
                            if(it.isSuccessful){
                                reference.pharmaReference.child("pharmacyNames").child(pharmacyName.toString() + ", " + location.toString()).setValue(reference.currentUserId.toString())
                                Toast.makeText(activity,"Account successfully created",Toast.LENGTH_LONG).show()
                                val i = Intent(activity, SignInActivity::class.java)
                                startActivity(i)
                                activity?.finish()
                            }
                        }
                    } else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}