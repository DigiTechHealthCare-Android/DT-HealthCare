package com.example.dgtechhealthcare.patient

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.doctorPrescribeMedicine.DoctorPrescribeMedicineFragment
import com.example.dgtechhealthcare.editProfile.EditPatientProfileFragment
import com.example.dgtechhealthcare.nurse.model.NurseData
import com.example.dgtechhealthcare.patient.presenter.PatientUploadClass
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.utils.ViewImageActivity
import com.example.dgtechhealthcare.utils.ViewPdfActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*

class PatientProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var uploadClassReference : PatientUploadClass
    lateinit var currentPhotoPath: String

    val REQUEST_IMAGE_CAPTURE = 1

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        userKey = arguments?.getString("userKey","")
        //from = arguments?.getString("from","")!!

        if(userKey?.isNotEmpty() == true){
            userID = userKey.toString()
        }
        else userID = reference.currentUserId!!



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
            activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(
                R.anim.fade_in,R.anim.fade_out,R.anim.slide_in,R.anim.slide_out
            )
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
            val options = arrayOf<CharSequence>("Take from Camera","Upload from gallery","Cancel")
            val builder : AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle("Do you want to?")
            builder.setItems(options,DialogInterface.OnClickListener { dialog, which ->
                if(which == 0) {
                    dispatchTakePictureIntent()
                }
                if(which == 1) {
                    val gallery : Intent = Intent()
                    gallery.setAction(Intent.ACTION_GET_CONTENT)
                    gallery.setType("*/*")
                    choice = 2
                    startActivityForResult(gallery,galleryPick)
                }
            })
            builder.show()
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
                                var connection : URLConnection? = null
                                try{
                                    connection = URL(report).openConnection()
                                } catch (e : IOException){
                                    e.printStackTrace()
                                }
                                CoroutineScope(Dispatchers.IO).launch {
                                    val contentType = connection?.getHeaderField("Content-Type")
                                    val img = contentType?.startsWith("image/")
                                    if(img!!){
                                        activity?.runOnUiThread {
                                            Toast.makeText(activity,"it's an image",Toast.LENGTH_LONG).show()
                                            val i = Intent(activity,ViewImageActivity::class.java)
                                            i.putExtra("url",report)
                                            startActivity(i)
                                        }
                                    } else {
                                        val i = Intent(activity,ViewPdfActivity::class.java)
                                        i.putExtra("url",report)
                                        startActivity(i)
                                    }
                                }
                            }
                        })
                        builder.show()
                    } else Toast.makeText(activity,"No report found",Toast.LENGTH_LONG).show()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        }

        populateProfile(view)
    }

    private fun populateProfile(view: View) {
        reference.userReference.child(userID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")) {
                    val img = snapshot.child("profileImage").value.toString()
                    Glide.with(view).load(img).circleCrop().placeholder(R.drawable.loading0).into(userprofileImg)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            if(choice == 1) {
                imgUri = data.data!!
                uploadClassReference.uploadToStorage(reference,reference.currentUserId,imgUri,requireActivity())
                userprofileImg.setImageURI(imgUri)
            } else if(choice == 2) {
                reportUri = data.data!!
                uploadClassReference.uploadMedicalReport(reportUri,requireActivity())
            }
        } else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode== RESULT_OK) {

            val f : File = File(currentPhotoPath)
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                mediaScanIntent.data = Uri.fromFile(f)
                activity?.sendBroadcast(mediaScanIntent)

                uploadClassReference.uploadReportToFirebase(f.name,Uri.fromFile(f),requireActivity())
            }

        } else Toast.makeText(activity,"ERROR!!",Toast.LENGTH_LONG).show()
    }



    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        //val storageDir : File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun initializeValues(view: View) {
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

        reference = FirebasePresenter(requireView())
        uploadClassReference = PatientUploadClass(view)
    }
}