package com.example.dgtechhealthcare.editProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class EditContentManagerProfileFragment : Fragment() {

    lateinit var name : TextView
    lateinit var loc : TextView
    lateinit var email : TextView
    lateinit var phone : TextView
    lateinit var editB : TextView

    lateinit var reference : FirebasePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_content_manager_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intializeValues(view)

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                name.setText(snapshot.child("username").value.toString())
                loc.setText(snapshot.child("location").value.toString())
                email.setText(snapshot.child("email").value.toString())
                phone.setText(snapshot.child("contact").value.toString())
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        editB.setOnClickListener {
            val hm = HashMap<String,Any>()
            hm["username"] = name.text.toString()
            hm["contact"] = phone.text.toString()
            hm["location"] = loc.text.toString()
            hm["email"] = email.text.toString()
            reference.userReference.child(reference.currentUserId!!).updateChildren(hm).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(activity,"Profile Updated",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun intializeValues(view: View) {
        name = view.findViewById(R.id.editCMName)
        loc = view.findViewById(R.id.editCMLocation)
        email = view.findViewById(R.id.editCMEmail)
        phone = view.findViewById(R.id.editCMPhone)
        editB = view.findViewById(R.id.editCMButton)

        reference = FirebasePresenter(view)
    }
}