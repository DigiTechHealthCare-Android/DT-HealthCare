package com.example.dgtechhealthcare.pharmacist.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.pharmacist.model.PharmacistProfileData
import com.example.dgtechhealthcare.pharmacist.presenter.PharmacistPresenter
import com.example.dgtechhealthcare.pharmacist.presenter.UploadImagePresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter

class PharmacistProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var uploadReference : UploadImagePresenter
    lateinit var presenter : PharmacistPresenter

    lateinit var pharmacistProfileImg : ImageView
    lateinit var nameTextView : TextView
    lateinit var pharmacyNameTextView: TextView
    lateinit var mobileTextView : TextView
    lateinit var locationTextView : TextView
    lateinit var editButtonView : ImageView
    lateinit var cameraEdit : ImageView

    private val imagePick = 0
    lateinit var imageUri : Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pharmacist_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)


        val data = PharmacistProfileData(pharmacistProfileImg,nameTextView,pharmacyNameTextView,mobileTextView,locationTextView)
        presenter.populateProfile(data,activity)

        editButtonView.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val frag = EditPharmacistFragment()
            transaction?.replace(R.id.fragment_container_pharmacist, frag)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        cameraEdit.setOnClickListener {
            val image = Intent().setAction(Intent.ACTION_GET_CONTENT)
            image.setType("image/*")
            startActivityForResult(image, imagePick)
        }
    }

    private fun initializeValues(view: View) {
        reference = FirebasePresenter(view)
        presenter = PharmacistPresenter(view)
        uploadReference = UploadImagePresenter(view)

        pharmacistProfileImg = view.findViewById(R.id.pharmacistIV)
        nameTextView = view.findViewById(R.id.usernameTV)
        pharmacyNameTextView = view.findViewById(R.id.pharmacyNameTV)
        mobileTextView = view.findViewById(R.id.contactTV)
        locationTextView = view.findViewById(R.id.locationTV)
        editButtonView = view.findViewById(R.id.editBV)
        cameraEdit = view.findViewById(R.id.pharmacistCameraEdit)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imagePick && resultCode == Activity.RESULT_OK && data != null){
            imageUri = data.data!!
            uploadReference.uploadImageToStorage(imageUri,requireActivity())

            pharmacistProfileImg.setImageURI(imageUri)
        }
    }


}