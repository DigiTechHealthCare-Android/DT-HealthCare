package com.example.dgtechhealthcare.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.view.fragments.PharmacistProfileFragment
import com.example.dgtechhealthcare.view.fragments.RequestFragment
import com.example.dgtechhealthcare.view.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class PharmacistNavigationActivity : AppCompatActivity() {

    val pharmaProfileFragment = PharmacistProfileFragment()
    val pharmaSettingsFragment = SettingsFragment()
    val pharmaRequestFragment = RequestFragment()

    lateinit var pharmaBottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pharmacist_navigation)



        pharmaBottomNavigationView = findViewById(R.id.pharmaNavigationView)
        replaceFragment(pharmaRequestFragment)

        pharmaBottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.pharmaProfile -> replaceFragment(pharmaProfileFragment)
                R.id.pharmaRequest -> replaceFragment(pharmaRequestFragment)
                R.id.pharmaSettings -> replaceFragment(pharmaSettingsFragment)

            }
            true
        }

    }
    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerPharmacist, fragment)
            transaction.commit()

        }
    }
}