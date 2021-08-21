package com.example.dgtechhealthcare.pharmacist.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.pharmacist.model.PharmacistData
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PharmacistProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter

    lateinit var pharmacistProfileImg : ImageView
    lateinit var nameTextView : TextView
    lateinit var mobileTextView : TextView
    lateinit var locationTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pharmacist_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        pharmacistProfileImg = view.findViewById(R.id.pharmacistIV)
        nameTextView = view.findViewById(R.id.usernameTV)
        mobileTextView = view.findViewById(R.id.contactTV)
        locationTextView = view.findViewById(R.id.locationTV)

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val pharmacist = snapshot.getValue(PharmacistData::class.java)

                nameTextView.text = pharmacist?.username
                mobileTextView.text = "Contact Number: ${pharmacist?.contact}"
                locationTextView.text = "Location: ${pharmacist?.location}"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}