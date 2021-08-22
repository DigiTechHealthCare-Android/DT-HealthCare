package com.example.dgtechhealthcare.nurse.model

data class ProfileData(val username: String ?= null,
                       val contact: String ?= null,
                       val hospital: String ?= null,
                       val profileImage: String ?= null,
                       val email: String ?= null,
                       val dateOfBirth: String ?= null,
                       val gender: String ?= null)