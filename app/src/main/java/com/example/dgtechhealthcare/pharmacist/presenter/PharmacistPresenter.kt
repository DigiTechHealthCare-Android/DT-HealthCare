package com.example.dgtechhealthcare.pharmacist.presenter

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.pharmacist.PharmacistInterface
import com.example.dgtechhealthcare.pharmacist.model.EditPharmacistData
import com.example.dgtechhealthcare.pharmacist.model.EditPharmacistModel
import com.example.dgtechhealthcare.pharmacist.model.PharmacistProfileData
import com.example.dgtechhealthcare.pharmacist.model.PharmacistProfileModel
import com.example.dgtechhealthcare.pharmacist.view.EditPharmacistFragment

class PharmacistPresenter(view : View) : PharmacistInterface.Presenter.Pharma {

    val model = EditPharmacistModel(view)
    val profileModel = PharmacistProfileModel(view)
    val editView = EditPharmacistFragment()

    fun editPharmacist(pharmacyDetails : EditPharmacistData,view: View){
        model.editPharmacistInfo(pharmacyDetails,view)
    }

    fun populateProfile(data: PharmacistProfileData, activity: FragmentActivity?) {
        profileModel.populateProfile(data,activity)
    }

    override fun updatedText(context: Context) {
        editView.updatedText(context)
    }

    override fun nameEmpty(context: Context) {
        editView.nameEmpty(context)
    }

    override fun pharmacyEmpty(context: Context) {
        editView.pharmacyEmpty(context)
    }

    override fun mobileEmpty(context: Context) {
        editView.mobileEmpty(context)
    }

    override fun dobEmpty(context: Context) {
        editView.dobEmpty(context)
    }
}