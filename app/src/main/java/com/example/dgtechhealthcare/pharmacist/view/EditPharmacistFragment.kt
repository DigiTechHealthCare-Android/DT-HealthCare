package com.example.dgtechhealthcare.pharmacist.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.pharmacist.PharmacistInterface
import com.example.dgtechhealthcare.pharmacist.model.EditPharmacistData
import com.example.dgtechhealthcare.pharmacist.presenter.PharmacistPresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter
import kotlinx.android.synthetic.main.fragment_edit_pharmacist.*

class EditPharmacistFragment : Fragment(),PharmacistInterface.View.EditProfile {

    lateinit var reference: FirebasePresenter
    lateinit var presenter : PharmacistPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_pharmacist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        val data = EditPharmacistData(editPharmacistName,editPharmacyName,editPharmacistContact
            ,editPharmacistLocation,editPharmacistEmail,updatePharmacistB)

        presenter.editPharmacist(data,view)
    }

    private fun initializeValues(view: View) {
        reference = FirebasePresenter(view)
        presenter = PharmacistPresenter(view)
    }

    override fun updatedText(context: Context) {
        Toast.makeText(context, R.string.profile_updated, Toast.LENGTH_SHORT).show()
    }

    override fun nameEmpty(context: Context) {
        Toast.makeText(context, R.string.name_empty, Toast.LENGTH_LONG).show()
    }

    override fun pharmacyEmpty(context: Context) {
        Toast.makeText(context, R.string.pharmacy_empty, Toast.LENGTH_LONG).show()
    }

    override fun mobileEmpty(context: Context) {
        Toast.makeText(context, R.string.invalid_mobile, Toast.LENGTH_LONG).show()
    }

    override fun dobEmpty(context: Context) {
        Toast.makeText(context, R.string.dob_empty, Toast.LENGTH_LONG).show()
    }
}