package com.example.dgtechhealthcare.nurse.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.nurse.model.ProfileData
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class editNurseProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter

    lateinit var nameTextView : TextView
    lateinit var mobileTextView : TextView
    lateinit var hospitalNameTextView : TextView
    lateinit var emailTextView: TextView
    lateinit var dobTextView: TextView
    lateinit var genderTextView: RadioGroup
    lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_nurse_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        nameTextView = view.findViewById(R.id.editPharmacistName)
        mobileTextView = view.findViewById(R.id.editPharmacistContact)
        hospitalNameTextView = view.findViewById(R.id.editPharmacyName)
        emailTextView = view.findViewById(R.id.editPharmacistEmail)
        dobTextView = view.findViewById(R.id.editPharmacistLocation)
        genderTextView = view.findViewById(R.id.editNurseGender)
        updateButton = view.findViewById(R.id.updatePharmacistB)

        // get data from Firebase
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nurseData = snapshot.getValue(ProfileData::class.java)

                nameTextView.text = nurseData?.username
                mobileTextView.text = "${nurseData?.contact}"
                hospitalNameTextView.text = "${nurseData?.hospital}"
                emailTextView.text = "${nurseData?.email}"
                dobTextView.text = "${nurseData?.dateOfBirth}"
//                genderTextView.text = "${nurseData?.gender}"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        var genderP = ""
        genderTextView.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.editPMale) genderP = "Male"
            if (checkedId == R.id.editPFemale) genderP = "Female"
            if (checkedId == R.id.editPOther) genderP = "Other"
        }

        // update data on clicking update button

        updateButton.setOnClickListener {
            //updateData()
            if(nameTextView.text.isEmpty()) Toast.makeText(activity,"Name is empty", Toast.LENGTH_LONG).show()
            else if(hospitalNameTextView.text.isEmpty()) Toast.makeText(activity,"Hospital name is empty", Toast.LENGTH_LONG).show()
            else if(mobileTextView.text.length > 10)  Toast.makeText(activity,"Invalid mobile number", Toast.LENGTH_LONG).show()
            else if (dobTextView.text.isEmpty()) Toast.makeText(activity,"Date of Birth is empty", Toast.LENGTH_LONG).show()
            else {
                val nurseProfileData = HashMap<String,Any>()
                nurseProfileData["username"] = nameTextView.text.toString()
                nurseProfileData["email"] = reference.auth.currentUser?.email.toString()
                nurseProfileData["hospital"] = hospitalNameTextView.text.toString()
                nurseProfileData["contact"] = mobileTextView.text.toString()
                nurseProfileData["dateOfBirth"] = dobTextView.text.toString()
                nurseProfileData["gender"] = genderP
                nurseProfileData["accountType"] = "nurse"

                reference.userReference.child(reference.currentUserId!!).updateChildren(nurseProfileData).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(activity, "Updated Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val frag = NurseProfileFragment()
            transaction?.replace(R.id.fragmentContainerNurse, frag)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }
}