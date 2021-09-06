package com.example.dgtechhealthcare.doctor

import android.net.Uri
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.utils.FirebasePresenter

class DoctorProfilePresenter(view : View) : DoctorProfileContract.Presenter {

    val model = DoctorProfileModel(view)
    val view = DoctorProfileFragment()

    fun populateProfile(data: DoctorProfileData) {
        model.populateDoctorProfile(data)
    }

    fun uploadProfilePicture(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri, requireActivity: FragmentActivity){
        model.uploadToStorage(reference, currentUserId, imgUri, requireActivity)
    }

    override fun showToast(requireActivity: FragmentActivity) {
        view.showImageUpload(requireActivity)
    }


}