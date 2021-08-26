package com.example.dgtechhealthcare.nurse.model

data class NurseData(val nuid: String ?= null,
                        val name: String ?= null,
                        val hospitalName: String ?= null,
                        val patients: Patients ?= null)

data class Patients(var puid: String ?= null,
                    var pName: String ?= null)
