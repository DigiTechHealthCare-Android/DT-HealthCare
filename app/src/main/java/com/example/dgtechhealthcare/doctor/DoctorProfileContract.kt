package com.example.dgtechhealthcare.doctor

import androidx.fragment.app.FragmentActivity

class DoctorProfileContract {

    interface View {

        fun showImageUpload(requireActivity: FragmentActivity)
    }

    interface Presenter {

        fun showToast(requireActivity: FragmentActivity)
    }
}