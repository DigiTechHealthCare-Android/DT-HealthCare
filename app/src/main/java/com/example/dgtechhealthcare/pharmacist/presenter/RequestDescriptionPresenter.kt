package com.example.dgtechhealthcare.pharmacist.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.pharmacist.model.DescriptionData
import com.example.dgtechhealthcare.pharmacist.model.RequestDescriptionModel

class RequestDescriptionPresenter(view : View) {

    val model = RequestDescriptionModel(view)

    fun populateDescription(type: String, descriptionData: DescriptionData,
                            requireActivity: FragmentActivity, userID: String) {

        model.populateDescription(type,descriptionData,requireActivity,userID)
    }

}