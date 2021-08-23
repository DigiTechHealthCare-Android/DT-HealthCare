package com.example.dgtechhealthcare.pharmacist.model

data class PharmacistData(val profileImage: String ?= null,
                          val username: String ?= null,
                          val pharmacyName: String ?= null,
                          val contact: String ?= null,
                          val email: String ?= null,
                          val location: String ?= null)