package com.example.dgtechhealthcare.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import com.example.dgtechhealthcare.SignInActivity

class LogoutClass {

    fun logoutUser(reference: FirebasePresenter, context: Context, activity: Activity, finish: Unit){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        builder.setTitle("Do you want to logout?")
        builder.setPositiveButton("Yes, Logout",
            DialogInterface.OnClickListener { dialog, which ->
                reference.auth.signOut()
                val i = Intent(context, SignInActivity::class.java)
                context.startActivity(i)
                dialog.dismiss()
                activity.finish()
            })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
        })
        dialog.show()
    }
}