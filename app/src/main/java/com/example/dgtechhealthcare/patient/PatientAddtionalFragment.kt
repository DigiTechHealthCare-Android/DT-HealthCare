package com.example.dgtechhealthcare.patient

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.utils.ViewPdfActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PatientAddtionalFragment : Fragment() {

    lateinit var fname : TextView
    lateinit var mname : TextView
    lateinit var address : TextView
    lateinit var dname : TextView
    lateinit var hname : TextView
    lateinit var viewHistory : TextView
    lateinit var upload : Button

    lateinit var reference : FirebasePresenter
    var patientID = ""
    var userType = ""
    var galleryPick : Int = 0
    var imgUri : Uri = Uri.parse("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_addtional, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        patientID = arguments?.getString("patientID","")!!

        if(patientID.isNullOrEmpty()){
            userType = reference.currentUserId!!
        } else userType = patientID

        var report = ""
        viewHistory.setOnClickListener {
            reference.userReference.child(userType).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild("medicalHistory")) {
                        report = snapshot.child("medicalHistory").value.toString()
                        val options = arrayOf<CharSequence>("Download","View","Cancel")
                        val builder : AlertDialog.Builder = AlertDialog.Builder(activity)
                        builder.setTitle("Do you want to?")
                        builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                            if(which == 0) {
                                val i = Intent(Intent.ACTION_VIEW,Uri.parse(report))
                                startActivity(i)
                            }
                            if(which == 1) {
                                val i = Intent(activity, ViewPdfActivity::class.java)
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

        upload.setOnClickListener {
            val gallery : Intent = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("*/*")
            startActivityForResult(gallery,galleryPick)
        }

        populateView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.data!!
            uploadToStorage(imgUri,requireActivity())
        } else Toast.makeText(activity,"ERROR!!", Toast.LENGTH_LONG).show()
    }

    private fun uploadToStorage(reportUri: Uri, requireActivity: FragmentActivity) {
        val resultUri = reportUri
        val path = reference.oldReportRef.child("${reference.currentUserId}.pdf")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(activity,"Report Uploaded",Toast.LENGTH_SHORT).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(reference.currentUserId!!).child("medicalHistory").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,"Report Uploaded",Toast.LENGTH_SHORT).show()
                    }
                }
            }else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateView() {
        reference.userReference.child(userType).addValueEventListener(object : ValueEventListener{
            var accountType = ""
            override fun onDataChange(snapshot: DataSnapshot) {
                accountType = snapshot.child("accountType").value.toString()
                if(accountType.compareTo("patient")==0 && userType.compareTo(reference.currentUserId!!)==0){

                } else if(accountType.compareTo("doctor")==0 || userType.compareTo(reference.currentUserId!!)!=0) {
                    upload.visibility = View.INVISIBLE
                }

                fname.setText(snapshot.child("fatherName").value.toString())
                mname.setText(snapshot.child("motherName").value.toString())
                address.setText(snapshot.child("otherDetes").value.toString())
                dname.setText(snapshot.child("doctorName").value.toString())
                hname.setText(snapshot.child("hostpitalName").value.toString())
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initializeValues(view: View) {
        fname = view.findViewById(R.id.addFName)
        mname = view.findViewById(R.id.addMName)
        address = view.findViewById(R.id.addAddress)
        dname = view.findViewById(R.id.addDoctorName)
        hname = view.findViewById(R.id.addHospitalName)
        viewHistory = view.findViewById(R.id.viewHistory)
        upload = view.findViewById(R.id.uploadHistory)

        reference = FirebasePresenter(view)
    }
}