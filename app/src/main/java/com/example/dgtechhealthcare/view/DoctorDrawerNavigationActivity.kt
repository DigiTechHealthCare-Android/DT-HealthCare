package com.example.dgtechhealthcare.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.SignInActivity
import com.example.dgtechhealthcare.doctor.DoctorProfileFragment
import com.example.dgtechhealthcare.editProfile.EditDoctorProfileFragment
import com.example.dgtechhealthcare.patientInfo.PatientInfoFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.utils.SettingsFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_doctor_drawer_navigation.*
import kotlinx.android.synthetic.main.doctor_nav_toolbar.*

class DoctorDrawerNavigationActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var reference: FirebasePresenter

    lateinit var doctorName: TextView
    lateinit var doctorEmail: TextView
    lateinit var doctorIV: ImageView
    lateinit var editUser: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_drawer_navigation)
        setSupportActionBar(toolbarDoctor)

        val toggle = ActionBarDrawerToggle(this, drawerLayoutDoctor, toolbarDoctor,
                R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        drawerLayoutDoctor.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<NavigationView>(R.id.navMenuDoctor)
        val headerView = navView.getHeaderView(0)

        doctorName = headerView.findViewById(R.id.drawerDoctorName)
        doctorEmail = headerView.findViewById(R.id.drawerDoctorEmail)
        doctorIV = headerView.findViewById(R.id.drawerDoctorIV)
        editUser = headerView.findViewById(R.id.editImageVIewDoctor)

        editUser.setOnClickListener {
            drawerLayoutDoctor.closeDrawer(GravityCompat.START)
            setToolbarTitle("Profile")
            changeFragment(EditDoctorProfileFragment())
        }

        reference = FirebasePresenter(View(this))
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")) {
                    val img = snapshot.child("profileImage").value.toString()
                    Picasso.get().load(img).into(doctorIV)
                }
                val name = snapshot.child("username").value.toString()
                val email = snapshot.child("email").value.toString()

                doctorName.setText(name)
                doctorEmail.setText(email)
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        navMenuDoctor.setNavigationItemSelectedListener(this)

        setToolbarTitle("Patient Information")
        changeFragment(PatientInfoFragment())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayoutDoctor.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.profileDr -> {
                setToolbarTitle("Profile")
                changeFragment(DoctorProfileFragment())
            }
            R.id.patientInfoDr -> {
                setToolbarTitle("Patient Information")
                changeFragment(PatientInfoFragment())
            }
            R.id.settingsDr -> {
                setToolbarTitle("Settings")
                changeFragment(SettingsFragment())
            }
            R.id.logoutDr -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Do you want to logout?")
                builder.setPositiveButton("Yes, Logout",
                    DialogInterface.OnClickListener { dialog, which ->
                        reference.auth.signOut()
                        val i = Intent(this, SignInActivity::class.java)
                        startActivity(i)
                        finish()
                    })
                builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                })
                builder.show()
            }
        }
       return true

        }

    private fun changeFragment(frag: Fragment) {
        val fragment = supportFragmentManager.beginTransaction()
        fragment.replace(R.id.fragment_container_doctor,frag).commit()
    }

    private fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }
}