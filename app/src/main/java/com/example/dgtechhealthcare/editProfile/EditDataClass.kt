package com.example.dgtechhealthcare.editProfile

import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup

data class PatientClass(var name : EditText, var mob : EditText, var dob:EditText,
                         var gender:RadioGroup, var father: EditText, var mother: EditText,
                         var details:EditText, var doctor: EditText, var hospital: EditText,
                        var image:ImageView,var upload:Button)

data class DoctorClass(var name: EditText,var contact: EditText,
                       var hospital: EditText,var specialization:EditText)


