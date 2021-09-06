package com.example.dgtechhealthcare.doctorPrescribeMedicine

import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText

data class PrescrideMedicine(var morningMed : EditText, var afternoonMed : EditText,
                             var eveningMed : EditText, var nightMed : EditText,
                             var prescribeB : Button, var morningCheckBox: CheckBox,
                             var afternoonCheckBox: CheckBox, var eveningCheckBox: CheckBox,
                             var nightCheckBox: CheckBox)
