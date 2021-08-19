package com.example.dgtechhealthcare.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.view.fragments.NurseArticleFragment
import com.example.dgtechhealthcare.view.fragments.NurseProfileFragment
import com.example.dgtechhealthcare.view.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class NurseNavigationActivity : AppCompatActivity() {

    val nurseProfileFragment = NurseProfileFragment()
    val nurseSettingsFragment = SettingsFragment()
    val articleFragment = NurseArticleFragment()

    lateinit var nurseBottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nurse_navigation)

        nurseBottomNavigationView = findViewById(R.id.nurseNavigationView)

        nurseBottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nurseProfile -> replaceFragment(nurseProfileFragment)
                R.id.nurseArticle -> replaceFragment(articleFragment)
                R.id.nurseSettings -> replaceFragment(nurseSettingsFragment)

            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerNurse, fragment)
            transaction.commit()

        }
    }
}