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
import com.example.dgtechhealthcare.patientInfo.PatientInfoFragment
import com.example.dgtechhealthcare.pharmacist.HistoryFragment
import com.example.dgtechhealthcare.pharmacist.RequestTabbedActivity
import com.example.dgtechhealthcare.pharmacist.view.PharmacistProfileFragment
import com.example.dgtechhealthcare.pharmacist.view.RequestFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.view.fragments.DoctorProfileFragment
import com.example.dgtechhealthcare.view.fragments.SettingsFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_doctor_drawer_navigation.*
import kotlinx.android.synthetic.main.activity_pharmacist_drawer_navigation.*
import kotlinx.android.synthetic.main.doctor_nav_toolbar.*
import kotlinx.android.synthetic.main.pharmacist_nav_toolbar.*

class PharmacistDrawerNavigationActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var reference: FirebasePresenter

    lateinit var pharmacistName: TextView
    lateinit var pharmacistEmail: TextView
    lateinit var pharmacistIV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pharmacist_drawer_navigation)

        setSupportActionBar(toolbarPharmacist)

        val toggle =
            ActionBarDrawerToggle(
                this,
                drawerLayoutPharmacist,
                toolbarPharmacist,
                R.string.open,
                R.string.close
            )
        toggle.isDrawerIndicatorEnabled = true
        drawerLayoutPharmacist.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<NavigationView>(R.id.navMenuPharmacist)
        val headerView = navView.getHeaderView(0)

        pharmacistName = headerView.findViewById(R.id.drawerPharmacistName)
        pharmacistEmail = headerView.findViewById(R.id.drawerPharmacistEmail)
        pharmacistIV = headerView.findViewById(R.id.drawerPharmacistIV)

        reference = FirebasePresenter(View(this))
//        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object:
//            ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.hasChild("profileImage")) {
//                    val img = snapshot.child("profileImage").value.toString()
//                    Picasso.get().load(img).into(pharmacistIV)
//                }
//                val name = snapshot.child("username").value.toString()
//                val email = snapshot.child("email").value.toString()
//
//                pharmacistName.setText(name)
//                pharmacistEmail.setText(email)
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//
//        })

        navMenuPharmacist.setNavigationItemSelectedListener(this)

        setToolbarTitle("Profile")
        changeFragment(PharmacistProfileFragment())

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayoutPharmacist.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.profilePharma -> {
                setToolbarTitle("Profile")
                changeFragment(PharmacistProfileFragment())
            }
            R.id.oldReqPharma -> {
                setToolbarTitle("Old Request")
               changeFragment(HistoryFragment())
            }
            R.id.requestPharma-> {
                setToolbarTitle("Requests")
               changeFragment(RequestFragment())
            }
            R.id.settingsPharma -> {
                setToolbarTitle("Settings")
                changeFragment(SettingsFragment())
            }
            R.id.logoutPharma -> {
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
        fragment.replace(R.id.fragment_container_pharmacist,frag).commit()
    }

    private fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }
}