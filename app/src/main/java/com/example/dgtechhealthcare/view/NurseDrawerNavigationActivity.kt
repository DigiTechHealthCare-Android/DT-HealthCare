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
import com.example.dgtechhealthcare.nurse.view.NursePatientFragment
import com.example.dgtechhealthcare.nurse.view.NurseProfileFragment
import com.example.dgtechhealthcare.nurse.view.editNurseProfileFragment
import com.example.dgtechhealthcare.patientInfo.PatientInfoFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.view.fragments.DoctorProfileFragment
import com.example.dgtechhealthcare.view.fragments.SettingsFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_doctor_drawer_navigation.*
import kotlinx.android.synthetic.main.activity_nurse_drawer_navigation.*
import kotlinx.android.synthetic.main.doctor_nav_toolbar.*
import kotlinx.android.synthetic.main.nurse_nav_toolbar.*

class NurseDrawerNavigationActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var reference: FirebasePresenter

    lateinit var nurseName: TextView
    lateinit var nurseEmail: TextView
    lateinit var nurseIV: ImageView
    lateinit var editB : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nurse_drawer_navigation)
        setSupportActionBar(toolbarNurse)

        val toggle =
            ActionBarDrawerToggle(
                this,
                drawerLayoutNurse,
                toolbarNurse,
                R.string.open,
                R.string.close
            )
        toggle.isDrawerIndicatorEnabled = true
        drawerLayoutNurse.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<NavigationView>(R.id.navMenuNurse)
        val headerView = navView.getHeaderView(0)

        nurseName = headerView.findViewById(R.id.drawerNurseName)
        nurseEmail = headerView.findViewById(R.id.drawerNurseEmail)
        nurseIV = headerView.findViewById(R.id.drawerNurseIV)
        editB = headerView.findViewById(R.id.editImageViewNurse)

        editB.setOnClickListener {
            drawerLayoutNurse.closeDrawer(GravityCompat.START)
            setToolbarTitle("Nurse Profile")
            changeFragment(editNurseProfileFragment())
        }

        reference = FirebasePresenter(View(this))
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")) {
                    val img = snapshot.child("profileImage").value.toString()
                    Picasso.get().load(img).into(nurseIV)
                }
                val name = snapshot.child("username").value.toString()
                val email = snapshot.child("email").value.toString()

                nurseName.setText(name)
                nurseEmail.setText(email)
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        navMenuNurse.setNavigationItemSelectedListener(this)

        setToolbarTitle("Patients")
        changeFragment(NursePatientFragment())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayoutNurse.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.profileNurse -> {
                setToolbarTitle("Profile")
                changeFragment(NurseProfileFragment())
            }
            R.id.articleNurse -> {
                setToolbarTitle("Patients")
                changeFragment(NursePatientFragment())
            }
            R.id.settingsNurse -> {
                setToolbarTitle("Settings")
                changeFragment(SettingsFragment())
            }
            R.id.logoutNurse -> {
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
                    finish()
                })
                builder.show()
            }
        }
        return true

    }
    private fun changeFragment(frag: Fragment) {
        val fragment = supportFragmentManager.beginTransaction()
        fragment.replace(R.id.fragment_container_nurse,frag).commit()
    }

    private fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }
}