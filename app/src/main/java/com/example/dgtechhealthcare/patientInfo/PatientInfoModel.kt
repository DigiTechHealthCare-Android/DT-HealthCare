package com.example.dgtechhealthcare.patientInfo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.view.fragments.PatientProfileFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class PatientInfoModel(val view:View) {

    val reference = FirebasePresenter(view)

    fun displayAllPatients(patientList:RecyclerView,activity:FragmentActivity){

        val options = FirebaseRecyclerOptions.Builder<PatientInfoDataClass>()
            .setQuery(reference.doctorReference.child(reference.currentUserId!!)
                .child("patients"),PatientInfoDataClass::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<PatientInfoDataClass, PatientViewHolder> =
            object : FirebaseRecyclerAdapter<PatientInfoDataClass,PatientViewHolder>(options){
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): PatientViewHolder {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.all_patient_layout, parent, false)
                    return PatientViewHolder(view)
                }

                override fun onBindViewHolder(holder: PatientViewHolder,position: Int,
                    model: PatientInfoDataClass) {
                    val userID = getRef(position).key
                    reference.userReference.child(userID!!).addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val username = snapshot.child("username").value.toString()
                            val contact = snapshot.child("contactNo").value.toString()
                            if(snapshot.hasChild("profileImage")) {
                                val profileImg = snapshot.child("profileImage").value.toString()
                                Picasso.get().load(profileImg).into(holder.imgV)
                            }
                            holder.usernameT.setText(username)
                            holder.contactT.setText(contact)
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                    holder.cardView.setOnClickListener {
                        //Toast.makeText(it.context,"${userID}",Toast.LENGTH_LONG).show()
                        val frag = PatientProfileFragment()
                        val bundle = Bundle()
                        bundle.putString("userKey",userID.toString())
                        bundle.putString("from","doctor")
                        frag.arguments = bundle
                        activity?.supportFragmentManager.beginTransaction()
                            .replace(R.id.doctorPatientFrame,frag)
                            ?.addToBackStack(null)?.commit()
                    }
                }
            }
        patientList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val usernameT = itemView.findViewById<TextView>(R.id.patientCardName)
        val contactT = itemView.findViewById<TextView>(R.id.patientCardContact)
        val imgV = itemView.findViewById<ImageView>(R.id.patientCardIV)
        val cardView = itemView.findViewById<CardView>(R.id.patientCV)

    }

}