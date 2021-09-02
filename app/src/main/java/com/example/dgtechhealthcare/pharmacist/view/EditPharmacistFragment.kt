package com.example.dgtechhealthcare.pharmacist.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.pharmacist.model.EditPharmacistData
import com.example.dgtechhealthcare.pharmacist.presenter.PharmacistPresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter

class EditPharmacistFragment : Fragment() {

    lateinit var reference: FirebasePresenter
    lateinit var presenter : PharmacistPresenter

    lateinit var nameTextView: TextView
    lateinit var mobileTextView: TextView
    lateinit var pharmacyNameTextView: TextView
    lateinit var emailTextView: TextView
    lateinit var locationTextView: TextView
    lateinit var updateButton: Button

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

        val data = EditPharmacistData(nameTextView,pharmacyNameTextView,mobileTextView
            ,locationTextView,emailTextView,updateButton)

        presenter.editPharmacist(data,view)
    }

    private fun initializeValues(view: View) {
        reference = FirebasePresenter(view)

        nameTextView = view.findViewById(R.id.editPharmacistName)
        mobileTextView = view.findViewById(R.id.editPharmacistContact)
        pharmacyNameTextView = view.findViewById(R.id.editPharmacyName)
        emailTextView = view.findViewById(R.id.editPharmacistEmail)
        locationTextView = view.findViewById(R.id.editPharmacistLocation)
        updateButton = view.findViewById(R.id.updatePharmacistB)

        presenter = PharmacistPresenter(view)
    }
}