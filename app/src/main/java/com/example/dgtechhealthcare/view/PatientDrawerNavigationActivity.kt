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
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.android.material.navigation.NavigationView
import com.example.dgtechhealthcare.view.fragments.PatientArticleFragment
import com.example.dgtechhealthcare.view.fragments.PatientProfileFragment
import com.example.dgtechhealthcare.view.fragments.SettingsFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_patient_drawer_navigation.*
import kotlinx.android.synthetic.main.patient_nav_toolbar.*

class PatientDrawerNavigationActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var reference : FirebasePresenter

    lateinit var userName : TextView
    lateinit var userEmail : TextView
    lateinit var userIV : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_drawer_navigation)
        setSupportActionBar(toolbar)

        val toggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<NavigationView>(R.id.nav_menu)
        val headerView = navView.getHeaderView(0)

        userName = headerView.findViewById(R.id.drawerUserName)
        userEmail = headerView.findViewById(R.id.drawerUserEmail)
        userIV = headerView.findViewById(R.id.drawerUserIV)

        reference = FirebasePresenter(View(this))
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")) {
                    val img = snapshot.child("profileImage").value.toString()
                    Picasso.get().load(img).into(userIV)
                }
                val name = snapshot.child("username").value.toString()
                val email = snapshot.child("email").value.toString()

                userName.setText(name)
                userEmail.setText(email)
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        nav_menu.setNavigationItemSelectedListener(this)

        setToolbarTitle("Patient Article")
        changeFragment(PatientArticleFragment())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.profilePatient -> {
                setToolbarTitle("Patient Profile")
                changeFragment(PatientProfileFragment())
            }
            R.id.articlePatient ->{
                setToolbarTitle("Patient Article")
                changeFragment(PatientArticleFragment())
            }
            R.id.settingsPatient ->{
                setToolbarTitle("Settings")
                changeFragment(SettingsFragment())
            }
            R.id.logout ->{
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
        fragment.replace(R.id.fragment_container,frag).commit()
    }

    private fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

}

