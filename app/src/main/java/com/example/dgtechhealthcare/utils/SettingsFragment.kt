package com.example.dgtechhealthcare.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.signin.SignInActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        settingsUserData(reference,reference.currentUserId!!,requireActivity())

        userLogoutB.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Do you want to logout?")
            builder.setPositiveButton("Yes, Logout",DialogInterface.OnClickListener { dialog, which ->
                reference.auth.signOut()
                val i = Intent(activity, SignInActivity::class.java)
                startActivity(i)
                activity?.finish()
            })
            builder.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, which ->
            })
            builder.show()
        }

        settingsAboutUs.setOnClickListener {
            val frag = AboutUsFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.settingsFrame,frag)
                ?.addToBackStack(null)?.commit()
        }

        settingsRate.setOnClickListener {
            try{
                val i = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse("market://details?id=com.example.dgtechhealthcare"))
                startActivity(i)
            } catch (e: Exception){
                Toast.makeText(activity,R.string.no_store,Toast.LENGTH_LONG).show()
            }
        }

        settingsFeedback.setOnClickListener {
            val i = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:dgtech.health@gmail.com")
                putExtra(Intent.EXTRA_EMAIL,"dgtech.health@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Feedback")
            }
            if(i.resolveActivity(activity?.packageManager!!) != null) startActivity(i)
        }
    }

    fun settingsUserData(reference:FirebasePresenter,currentUserId:String,activity: Context){

        reference.userReference.child(currentUserId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")) {
                    val img = snapshot.child("profileImage").value.toString()
                    Picasso.get().load(img).into(settingsUserIV)
                }
                if(snapshot.hasChild("username")) {
                    val name = snapshot.child("username").value.toString()
                    settingsNameT.text = name
                }
                if(snapshot.hasChild("email")) {
                    val email = snapshot.child("email").value.toString()
                    settingsEmailT.text = email
                }

            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}