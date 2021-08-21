package com.example.dgtechhealthcare.view.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.editProfile.EditPatientProfileFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.utils.ViewPdfActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class PatientProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter

    lateinit var userprofileImg : ImageView
    lateinit var editProfileIV : ImageView
    lateinit var username : TextView
    lateinit var mobile : TextView
    lateinit var userdob : TextView
    lateinit var usergender : TextView
    lateinit var viewReport : TextView
    lateinit var uploadReport : Button

    var galleryPick : Int = 0
    var choice = 0
    var imgUri : Uri = Uri.parse("")
    var reportUri : Uri = Uri.parse("")

    var userKey : String? = ""
    var from : String? = ""

    var userID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(requireView())

        userKey = arguments?.getString("userKey","")
        Toast.makeText(activity,"$userKey",Toast.LENGTH_LONG).show()
        //from = arguments?.getString("from","")!!

        if(userKey?.isNotEmpty() == true){
            userID = userKey.toString()
        }else userID = reference.currentUserId!!

        userprofileImg = view.findViewById(R.id.patientIV)
        editProfileIV = view.findViewById(R.id.editProfileB)
        username = view.findViewById(R.id.patientName)
        mobile = view.findViewById(R.id.patientMob)
        userdob = view.findViewById(R.id.patientDob)
        usergender = view.findViewById(R.id.patientGender)
        viewReport = view.findViewById(R.id.patientReport)
        uploadReport = view.findViewById(R.id.uploadReportB)

        if(userID?.compareTo(reference.currentUserId!!) != 0){
            userprofileImg.isClickable = false
            uploadReport.visibility = View.INVISIBLE
            editProfileIV.visibility = View.INVISIBLE
        }else {
            userprofileImg.isClickable = true
            uploadReport.visibility = View.VISIBLE
            editProfileIV.visibility = View.VISIBLE
        }

        userprofileImg.setOnClickListener {
            val gallery : Intent = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            choice = 1
            startActivityForResult(gallery,galleryPick)
        }

        editProfileIV.setOnClickListener {
            editProfilePT()
        }

        uploadReport.setOnClickListener {
            val gallery : Intent = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("*/*")
            choice = 2
            startActivityForResult(gallery,galleryPick)
        }

        var report = ""

        viewReport.setOnClickListener {
            reference.userReference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild("report")) {
                        report = snapshot.child("report").value.toString()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

            val options = arrayOf<CharSequence>("Download","View","Cancel")
            val builder : AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle("Do you want to?")
            builder.setItems(options,DialogInterface.OnClickListener { dialog, which ->
                if(which == 0) {
                    val i = Intent(Intent.ACTION_VIEW,Uri.parse(report))
                    startActivity(i)
                }
                if(which == 1) {
                    val i = Intent(activity,ViewPdfActivity::class.java)
                    i.putExtra("url",report)
                    startActivity(i)
                }
            })
            builder.show()
        }

        reference.userReference.child(userID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")) {
                    val img = snapshot.child("profileImage").value.toString()
                    Picasso.get().load(img).into(userprofileImg)
                }
                val name = snapshot.child("username").value.toString()
                val mob = snapshot.child("contactNo").value.toString()
                val dob = snapshot.child("dateOfBirth").value.toString()
                val gender = snapshot.child("gender").value.toString()

                username.text = name
                mobile.text = "Contact number : $mob"
                userdob.text = "Date of Birth : $dob"
                usergender.text = "Sex : $gender"
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun editProfilePT() {
        val frag = EditPatientProfileFragment()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.patientProfileFrame,frag)
            ?.addToBackStack(null)?.commit()
    }

    private fun uploadMedicalReport(reportUri : Uri) {
        val resultUri = reportUri
        val path = reference.userReportRef.child("${reference.currentUserId}.pdf")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(activity,"Report Uploaded",Toast.LENGTH_SHORT).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(reference.currentUserId!!).child("report").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,"Report Uploaded",Toast.LENGTH_SHORT).show()
                    }
                }
            }else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            if(choice == 1) {
                imgUri = data.data!!
                uploadToStorage(reference,reference.currentUserId,imgUri,requireActivity())
            } else if(choice == 2) {
                reportUri = data.data!!
                uploadMedicalReport(reportUri)
            }
        } else Toast.makeText(activity,"ERROR!!",Toast.LENGTH_LONG).show()
    }

    private fun uploadToStorage(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri, activity: Context) {

        val resultUri = imgUri
        val path = reference.userProfileImgRef.child("$currentUserId.jpg")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(activity,"Profile image changed",Toast.LENGTH_SHORT).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(currentUserId!!).child("profileImage").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,"Image stored",Toast.LENGTH_SHORT).show()
                    }
                }
            } else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
        }
        userprofileImg.setImageURI(imgUri)
    }
}