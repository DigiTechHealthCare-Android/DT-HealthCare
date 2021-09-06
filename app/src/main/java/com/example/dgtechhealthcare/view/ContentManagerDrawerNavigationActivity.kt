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
import com.example.dgtechhealthcare.signin.SignInActivity
import com.example.dgtechhealthcare.contentManager.CRUDContentActivity
import com.example.dgtechhealthcare.contentManager.ContentManagerProfileFragment
import com.example.dgtechhealthcare.editProfile.EditContentManagerProfileFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.utils.SettingsFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_content_manager_drawer_navigation.*
import kotlinx.android.synthetic.main.content_manager_nav_toolbar.*

class ContentManagerDrawerNavigationActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var reference: FirebasePresenter

    lateinit var contentManagerName: TextView
    lateinit var contentManagerEmail: TextView
    lateinit var contentManagerIV: ImageView
    lateinit var contentManagerEdit : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_manager_drawer_navigation)

        setSupportActionBar(toolbarContentManager)

        val toggle =
            ActionBarDrawerToggle(
                this,
                drawerLayoutCM,
                toolbarContentManager,
                R.string.open,
                R.string.close
            )
        toggle.isDrawerIndicatorEnabled = true
        drawerLayoutCM.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<NavigationView>(R.id.navMenuCM)
        val headerView = navView.getHeaderView(0)

        contentManagerName = headerView.findViewById(R.id.drawerCMName)
        contentManagerEmail = headerView.findViewById(R.id.drawerCMEmail)
        contentManagerIV = headerView.findViewById(R.id.drawerCMIV)
        contentManagerEdit = headerView.findViewById(R.id.editImageVIewCM)

        contentManagerEdit.setOnClickListener {
            drawerLayoutCM.closeDrawer(GravityCompat.START)
            setToolbarTitle("Edit Profile")
            changeFragment(EditContentManagerProfileFragment())
        }

        reference = FirebasePresenter(View(this))
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")){
                    val img = snapshot.child("profileImage").value.toString()
                    Picasso.get().load(img).into(contentManagerIV)
                }
                contentManagerName.setText(snapshot.child("username").value.toString())
                contentManagerEmail.setText(snapshot.child("email").value.toString())
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        navMenuCM.setNavigationItemSelectedListener(this)

        setToolbarTitle("Article")
        changeFragment(CRUDContentActivity())

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayoutCM.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.profileManager -> {
                setToolbarTitle("Profile")
                changeFragment(ContentManagerProfileFragment())
            }
            R.id.articleManager -> {
                setToolbarTitle("Article")
                changeFragment(CRUDContentActivity())
            }
            R.id.settingsManager -> {
                setToolbarTitle("Settings")
                changeFragment(SettingsFragment())
            }
            R.id.logoutManager -> {
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
        fragment.replace(R.id.fragment_container_content_manager,frag).commit()
    }

    private fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }
}