package com.example.dgtechhealthcare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.dgtechhealthcare.view.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class PatientsNavigationActivity : AppCompatActivity() {
    val patientProfileFragment = PatientProfileFragment()
    val patientSettingsFragment = SettingsFragment()
    val patientsArticleFragment = PatientArticleFragment()

    lateinit var patientBottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patients_navigation)

        patientBottomNavigationView = findViewById(R.id.patientNavigationView)

        patientBottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.patientProfile -> replaceFragment(patientProfileFragment)
                R.id.patientArticle -> replaceFragment(patientsArticleFragment)
                R.id.patientSettings -> replaceFragment(patientSettingsFragment)

            }
            true
        }

    }
    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerPatients, fragment)
            transaction.commit()

        }
    }
}