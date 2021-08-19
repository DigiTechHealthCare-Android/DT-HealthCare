package com.example.dgtechhealthcare.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.view.fragments.DoctorProfileFragment
import com.example.dgtechhealthcare.view.fragments.PatientInfoFragment
import com.example.dgtechhealthcare.view.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DoctorNavigationActivity : AppCompatActivity() {

    val docProfileFragment = DoctorProfileFragment()
    val docSettingsFragment = SettingsFragment()
    val patientInfoFrag = PatientInfoFragment()

    lateinit var doctorBottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_navigation)
        replaceFragment(docProfileFragment)
        doctorBottomNavigationView = findViewById(R.id.drNavigationView)

        doctorBottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.drProfile -> replaceFragment(docProfileFragment)
                R.id.drPatientInfo -> replaceFragment(patientInfoFrag)
                R.id.drSettings -> replaceFragment(docSettingsFragment)

            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()

        }
    }

}