package com.example.dgtechhealthcare.editProfile

import android.widget.EditText
import android.widget.RadioGroup

data class PatientEditData(var name : EditText,var mob : EditText,var dob:EditText,
                           var gender:RadioGroup,var father: EditText,var mother: EditText,
                           var details:EditText,var doctor: EditText,var hospital: EditText)
