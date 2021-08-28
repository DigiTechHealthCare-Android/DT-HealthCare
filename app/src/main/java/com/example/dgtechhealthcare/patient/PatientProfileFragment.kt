package com.example.dgtechhealthcare.patient

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
import com.example.dgtechhealthcare.doctorPrescribeMedicine.DoctorPrescribeMedicineFragment
import com.example.dgtechhealthcare.editProfile.EditPatientProfileFragment
import com.example.dgtechhealthcare.nurse.model.NurseData
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
    lateinit var viewPrescription : TextView
    lateinit var uploadReport : Button
    lateinit var prescribeMedB : Button
    lateinit var additianalInfo : TextView

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
        //from = arguments?.getString("from","")!!

        if(userKey?.isNotEmpty() == true){
            userID = userKey.toString()
        }
        else userID = reference.currentUserId!!

        userprofileImg = view.findViewById(R.id.patientIV)
        editProfileIV = view.findViewById(R.id.editProfileB)
        username = view.findViewById(R.id.patientName)
        mobile = view.findViewById(R.id.patientMob)
        userdob = view.findViewById(R.id.patientDob)
        usergender = view.findViewById(R.id.patientGender)
        viewReport = view.findViewById(R.id.patientReport)
        viewPrescription = view.findViewById(R.id.viewPrescription)
        uploadReport = view.findViewById(R.id.uploadReportB)
        prescribeMedB = view.findViewById(R.id.prescribeMedB)
        additianalInfo = view.findViewById(R.id.addtionalInfoTV)

        if(userID?.compareTo(reference.currentUserId!!) != 0){
            userprofileImg.isClickable = false
            uploadReport.visibility = View.INVISIBLE
            editProfileIV.visibility = View.INVISIBLE
            prescribeMedB.visibility = View.VISIBLE
        }
        else {
            userprofileImg.isClickable = true
            uploadReport.visibility = View.VISIBLE
            editProfileIV.visibility = View.VISIBLE
            prescribeMedB.visibility = View.INVISIBLE
        }

        additianalInfo.setOnClickListener {
            val frag = PatientAddtionalFragment()
            val bundle = Bundle()
            bundle.putString("patientID",userID)
            frag.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.patientProfileFrame,frag)
                ?.addToBackStack(null)?.commit()
        }

        viewPrescription.setOnClickListener {
            val frag = DoctorPrescribeMedicineFragment()
            val bundle = Bundle()
            bundle.putString("patientID",userID)
            frag.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.patientProfileFrame,frag)
                ?.addToBackStack(null)?.commit()
        }

        prescribeMedB.setOnClickListener {
            val frag = DoctorPrescribeMedicineFragment()
            val bundle = Bundle()
            bundle.putString("patientID",userID)
            frag.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.patientProfileFrame,frag)
                ?.addToBackStack(null)?.commit()
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
                    } else Toast.makeText(activity,"No report found",Toast.LENGTH_LONG).show()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

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
                mobile.text = "$mob"
                userdob.text = "$dob"
                usergender.text = "$gender"
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