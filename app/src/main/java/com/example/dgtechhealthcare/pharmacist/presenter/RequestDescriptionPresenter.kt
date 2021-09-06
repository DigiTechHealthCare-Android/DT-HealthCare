package com.example.dgtechhealthcare.pharmacist.presenter

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.pharmacist.PharmacistInterface
import com.example.dgtechhealthcare.pharmacist.model.DescriptionData
import com.example.dgtechhealthcare.pharmacist.model.RequestDescriptionModel
import com.example.dgtechhealthcare.pharmacist.view.RequestDescriptionFragment

class RequestDescriptionPresenter(view : View) : PharmacistInterface.Presenter.RequestDesc {

    val model = RequestDescriptionModel(view)
    val view = RequestDescriptionFragment()

    fun populateDescription(type: String, descriptionData: DescriptionData,
                            requireActivity: FragmentActivity, userID: String) {

        model.populateDescription(type,descriptionData,requireActivity,userID)
    }

    override fun requestApproved(context: Context) {
        view.requestApproved(context)
    }

    override fun requestDeclined(context: Context) {
        view.requestDeclined(context)
    }

}