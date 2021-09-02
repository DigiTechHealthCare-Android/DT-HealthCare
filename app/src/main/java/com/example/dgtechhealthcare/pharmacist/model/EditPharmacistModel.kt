package com.example.dgtechhealthcare.pharmacist.model

import android.view.View
import android.widget.Toast
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class EditPharmacistModel(view : View) {

    val reference = FirebasePresenter(view)

    fun editPharmacistInfo(pharmacyDetails: EditPharmacistData,view: View) {

        // get data from Firebase
        reference.userReference.child(reference.currentUserId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pharmacistData = snapshot.getValue(PharmacistData::class.java)

                    pharmacyDetails.username.text = pharmacistData?.username
                    pharmacyDetails.contact.text = pharmacistData?.contact
                    pharmacyDetails.name.text = pharmacistData?.pharmacyName
                    pharmacyDetails.email.text = pharmacistData?.email
                    pharmacyDetails.location.text = pharmacistData?.location
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        pharmacyDetails.updateB.setOnClickListener {
            //updateData()
            if (pharmacyDetails.username.text.isEmpty()) Toast.makeText(
                it.context, "Name is empty",
                Toast.LENGTH_LONG).show()
            else if (pharmacyDetails.name.text.isEmpty()) Toast.makeText(
                it.context, "Pharmacy name is empty",
                Toast.LENGTH_LONG).show()
            else if (pharmacyDetails.contact.text.length > 10) Toast.makeText(
                it.context, "Invalid mobile number",
                Toast.LENGTH_LONG).show()
            else if (pharmacyDetails.email.text.isEmpty()) Toast.makeText(
                it.context, "Date of Birth is empty",
                Toast.LENGTH_LONG).show()
            else {
                val pharmacistProfileData = HashMap<String, Any>()
                pharmacistProfileData["username"] = pharmacyDetails.username.text.toString()
                pharmacistProfileData["email"] = reference.auth.currentUser?.email.toString()
                pharmacistProfileData["contact"] = pharmacyDetails.contact.text.toString()
                pharmacistProfileData["pharmacyName"] = pharmacyDetails.name.text.toString()
                pharmacistProfileData["location"] = pharmacyDetails.location.text.toString()
                pharmacistProfileData["accountType"] = "pharmacist"

                reference.userReference.child(reference.currentUserId!!).updateChildren(pharmacistProfileData).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(view.context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}