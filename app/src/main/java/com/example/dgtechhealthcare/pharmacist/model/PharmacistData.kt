package com.example.dgtechhealthcare.pharmacist.model

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

data class PharmacistData(val profileImage: String ?= null,
                          val username: String ?= null,
                          val pharmacyName: String ?= null,
                          val contact: String ?= null,
                          val email: String ?= null,
                          val location: String ?= null)

data class EditPharmacistData(var username:TextView, var name:TextView,
                              var contact:TextView,var location:TextView,
                              var email : TextView,var updateB:Button)

data class PharmacistProfileData(var profileImage:ImageView,var username:TextView,var name:TextView,
                                 var contact:TextView,var location: TextView)

data class DescriptionData(var name:TextView,var image:ImageView,var med1:TextView,var med2:TextView,
                           var med3:TextView,var med4:TextView,var acceptB:TextView,var declineB:TextView)