package com.example.dgtechhealthcare.editProfile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_edit_content_manager_profile.*

class EditContentManagerProfileFragment : Fragment(),EditProfileContract.Manager {

    lateinit var reference : FirebasePresenter
    lateinit var presenter : EditProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_content_manager_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)
        presenter = EditProfilePresenter(view)

        val data = ManagerClass(editCMName,editCMPhone,editCMLocation,editCMEmail)
        presenter.populateEditContentManagerProfile(data)

        editCMButton.setOnClickListener {
            presenter.updateContentManagerProfile(data)
        }
    }

    override fun profileUpdated(context: Context) {
        Toast.makeText(context,R.string.profile_updated,Toast.LENGTH_LONG).show()
    }
}