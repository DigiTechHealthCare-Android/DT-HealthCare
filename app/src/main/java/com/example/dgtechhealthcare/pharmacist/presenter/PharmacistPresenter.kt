package com.example.dgtechhealthcare.pharmacist.presenter

import android.view.View
import com.example.dgtechhealthcare.pharmacist.model.EditPharmacistData
import com.example.dgtechhealthcare.pharmacist.model.EditPharmacistModel
import com.example.dgtechhealthcare.pharmacist.model.PharmacistProfileData
import com.example.dgtechhealthcare.pharmacist.model.PharmacistProfileModel

class PharmacistPresenter(view : View) {

    val model = EditPharmacistModel(view)
    val profileModel = PharmacistProfileModel(view)

    fun editPharmacist(pharmacyDetails : EditPharmacistData,view: View){
        model.editPharmacistInfo(pharmacyDetails,view)
    }

    fun populateProfile(data: PharmacistProfileData) {
        profileModel.populateProfile(data)
    }
}