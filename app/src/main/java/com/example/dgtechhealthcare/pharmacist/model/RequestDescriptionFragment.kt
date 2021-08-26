package com.example.dgtechhealthcare.pharmacist.model

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class RequestDescriptionFragment : Fragment() {

    lateinit var name : TextView
    lateinit var img : ImageView
    lateinit var med1 : TextView
    lateinit var med2 : TextView
    lateinit var med3 : TextView
    lateinit var med4 : TextView
    lateinit var acceptB : Button
    lateinit var declineB : Button

    var userID = ""
    var type = ""

    lateinit var reference: FirebasePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}

        userID = arguments?.getString("userID","")!!
        type = arguments?.getString("type","")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        populateDescription(type)
    }

    private fun populateDescription(type: String) {

        var m1 = ""
        var m2 = ""
        var m3 = ""
        var m4 = ""

        if (type.compareTo("requestHistory")==0){
            acceptB.visibility = View.GONE
            declineB.visibility = View.GONE
        }

        reference.userReference.child(userID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                name.setText(snapshot.child("username").value.toString())
                Picasso.get().load(snapshot.child("profileImage").value.toString()).into(img)
                if(type.compareTo("requestHistory")==0){
                    reference.pharmaReference.child(reference.currentUserId!!).child("requestHistory").child(userID).addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            m1 = snapshot.child("med1").value.toString()
                            m2 = snapshot.child("med2").value.toString()
                            m3 = snapshot.child("med3").value.toString()
                            m4 = snapshot.child("med4").value.toString()

                            med1.setText(m1)
                            med2.setText(m2)
                            med3.setText(m3)
                            med4.setText(m4)
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                } else {
                    reference.pharmaReference.child(reference.currentUserId!!).child("requests").child(userID).addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            m1 = snapshot.child("med1").value.toString()
                            m2 = snapshot.child("med2").value.toString()
                            m3 = snapshot.child("med3").value.toString()
                            m4 = snapshot.child("med4").value.toString()

                            med1.setText(m1)
                            med2.setText(m2)
                            med3.setText(m3)
                            med4.setText(m4)
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }

            }
            override fun onCancelled(error: DatabaseError) {}

        })

        acceptB.setOnClickListener {
            val hashMap = HashMap<String,Any>()
            hashMap["med1"] = m1
            hashMap["med2"] = m2
            hashMap["med3"] = m3
            hashMap["med4"] = m4

            reference.pharmaReference.child(reference.currentUserId!!).child("requestHistory").child(userID).updateChildren(hashMap).addOnCompleteListener {
                if(it.isSuccessful){
                    reference.pharmaReference.child(reference.currentUserId!!).child("requests").child(userID).removeValue().addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(activity,"Request Approved",Toast.LENGTH_LONG).show()
                            activity?.supportFragmentManager?.popBackStack()
                        }
                    }
                }
            }
        }

        declineB.setOnClickListener {
            reference.pharmaReference.child(reference.currentUserId!!).child("requests").child(userID).removeValue().addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(activity,"Request Declined",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initializeValues(view: View) {
        name = view.findViewById(R.id.requestName)
        img = view.findViewById(R.id.requestIV)
        med1 = view.findViewById(R.id.requestMed1)
        med2 = view.findViewById(R.id.requestMed2)
        med3 = view.findViewById(R.id.requestMed3)
        med4 = view.findViewById(R.id.requestMed4)
        acceptB = view.findViewById(R.id.requestAcceptB)
        declineB = view.findViewById(R.id.requestDeclineB)
        reference = FirebasePresenter(view)
    }

}