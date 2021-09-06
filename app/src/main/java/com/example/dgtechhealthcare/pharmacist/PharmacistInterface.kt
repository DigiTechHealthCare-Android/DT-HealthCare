package com.example.dgtechhealthcare.pharmacist

import android.content.Context

class PharmacistInterface {

    interface View{

        interface EditProfile{
            fun updatedText(context: Context)

            fun nameEmpty(context: Context)

            fun pharmacyEmpty(context: Context)

            fun mobileEmpty(context: Context)

            fun dobEmpty(context: Context)
        }

        interface RequestDescription{
            fun requestApproved(context: Context)

            fun requestDeclined(context: Context)
        }

    }

    interface Presenter {

        interface Pharma{
            fun updatedText(context: Context)

            fun nameEmpty(context: Context)

            fun pharmacyEmpty(context: Context)

            fun mobileEmpty(context: Context)

            fun dobEmpty(context: Context)
        }

        interface RequestDesc {
            fun requestApproved(context: Context)

            fun requestDeclined(context: Context)
        }
    }

}