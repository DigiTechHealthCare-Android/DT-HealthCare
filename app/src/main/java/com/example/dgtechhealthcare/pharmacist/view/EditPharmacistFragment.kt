package com.example.dgtechhealthcare.pharmacist.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.nurse.view.editNurseProfileFragment
import com.example.dgtechhealthcare.pharmacist.model.PharmacistData
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class EditPharmacistFragment : Fragment() {

    lateinit var reference: FirebasePresenter

    lateinit var nameTextView: TextView
    lateinit var mobileTextView: TextView
    lateinit var pharmacyNameTextView: TextView
    lateinit var emailTextView: TextView
    lateinit var locationTextView: TextView
    lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_pharmacist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        nameTextView = view.findViewById(R.id.editPharmacistName)
        mobileTextView = view.findViewById(R.id.editPharmacistContact)
        pharmacyNameTextView = view.findViewById(R.id.editPharmacyName)
        emailTextView = view.findViewById(R.id.editPharmacistEmail)
        locationTextView = view.findViewById(R.id.editPharmacistLocation)
        updateButton = view.findViewById(R.id.updatePharmacistB)

        // get data from Firebase
        reference.userReference.child(reference.currentUserId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pharmacistData = snapshot.getValue(PharmacistData::class.java)

                    nameTextView.text = pharmacistData?.username
                    mobileTextView.text = pharmacistData?.contact
                    pharmacyNameTextView.text = pharmacistData?.pharmacyName
                    emailTextView.text = pharmacistData?.email
                    locationTextView.text = pharmacistData?.location
                    //dobTextView.text = pharmacistData?.
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        updateButton.setOnClickListener {
            //updateData()
            if (nameTextView.text.isEmpty()) Toast.makeText(
                activity, "Name is empty",
                Toast.LENGTH_LONG).show()
            else if (nameTextView.text.isEmpty()) Toast.makeText(
                activity, "Hospital name is empty",
                Toast.LENGTH_LONG).show()
            else if (mobileTextView.text.length > 10) Toast.makeText(
                activity, "Invalid mobile number",
                Toast.LENGTH_LONG).show()
            else if (emailTextView.text.isEmpty()) Toast.makeText(
                activity, "Date of Birth is empty",
                Toast.LENGTH_LONG).show()
            else {
                val pharmacistProfileData = HashMap<String, Any>()
                pharmacistProfileData["username"] = nameTextView.text.toString()
                pharmacistProfileData["email"] = reference.auth.currentUser?.email.toString()
                pharmacistProfileData["contact"] = mobileTextView.text.toString()
                pharmacistProfileData["pharmacyName"] = pharmacyNameTextView.text.toString()
                pharmacistProfileData["location"] = locationTextView.text.toString()
                pharmacistProfileData["accountType"] = "pharmacist"

                reference.userReference.child(reference.currentUserId!!).updateChildren(pharmacistProfileData).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(activity, "Updated Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val frag = PharmacistProfileFragment()
            transaction?.replace(R.id.fragmentContainerPharmacist, frag)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }
}