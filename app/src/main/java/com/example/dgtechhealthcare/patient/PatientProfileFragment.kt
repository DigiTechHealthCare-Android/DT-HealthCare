package com.example.dgtechhealthcare.patient

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.doctorPrescribeMedicine.DoctorPrescribeMedicineFragment
import com.example.dgtechhealthcare.editProfile.EditPatientProfileFragment
import com.example.dgtechhealthcare.patient.presenter.PatientUploadClass
import com.example.dgtechhealthcare.patient.presenter.PatientViewReportPresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.utils.ViewImageActivity
import com.example.dgtechhealthcare.utils.ViewPdfActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_patient_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*

class PatientProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var uploadClassReference : PatientUploadClass
    lateinit var currentPhotoPath: String

    val REQUEST_IMAGE_CAPTURE = 1

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

        if(userKey?.isNotEmpty() == true){
            userID = userKey.toString()
        }
        else userID = reference.currentUserId!!



        if(userID?.compareTo(reference.currentUserId!!) != 0){
            patientIV.isClickable = false
            uploadReportB.visibility = View.INVISIBLE
            editProfileB.visibility = View.INVISIBLE
            prescribeMedB.visibility = View.VISIBLE
            patientCameraEdit.visibility = View.GONE
        }
        else {
            patientIV.isClickable = true
            uploadReportB.visibility = View.VISIBLE
            editProfileB.visibility = View.VISIBLE
            prescribeMedB.visibility = View.INVISIBLE
        }

        addtionalInfoTV.setOnClickListener {
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

        editProfileB.setOnClickListener {
            editProfilePT()
        }

        patientCameraEdit.setOnClickListener {
            val options = arrayOf("Camera","Gallery","Cancel")
            val builder = AlertDialog.Builder(activity)
            val a = builder.create()
            builder.setCancelable(false)
            builder.setTitle("Take Picture from")
            builder.setItems(options,DialogInterface.OnClickListener { dialog, which ->
                if (which ==0){
                    dispatchTakePictureIntent("image")
                    a.cancel()
                }
                if (which == 1) {
                    val gallery : Intent = Intent()
                    gallery.setAction(Intent.ACTION_GET_CONTENT)
                    gallery.setType("image/*")
                    choice = 1
                    startActivityForResult(gallery,galleryPick)
                }
                if (which ==2){
                    a.dismiss()
                }
            })
            builder.show()
        }

        uploadReportB.setOnClickListener {
            val options = arrayOf<CharSequence>("Take from Camera","Upload from gallery","Cancel")
            val builder : AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle("Do you want to?")
            builder.setItems(options,DialogInterface.OnClickListener { dialog, which ->
                if(which == 0) {
                    dispatchTakePictureIntent("report")
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

        patientReport.setOnClickListener {
            PatientViewReportPresenter().showReport(reference,userID,requireActivity(),requireContext(),"report")
        }

        populateProfile(view)
    }

    private fun populateProfile(view: View) {
        reference.userReference.child(userID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if(snapshot.hasChild("profileImage")) {
                        val img = snapshot.child("profileImage").value.toString()
                        Picasso.get().load(img).into(patientIV)
                    }
                }catch (e :Exception){}

                val name = snapshot.child("username").value.toString()
                val mob = snapshot.child("contactNo").value.toString()
                val dob = snapshot.child("dateOfBirth").value.toString()
                val gender = snapshot.child("gender").value.toString()

                patientName.text = name
                patientMob.text = mob
                patientDob.text = dob
                patientGender.text = gender
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
                patientIV.setImageURI(imgUri)
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

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            val f = File(currentPhotoPath)
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                mediaScanIntent.data = Uri.fromFile(f)
                activity?.sendBroadcast(mediaScanIntent)
                uploadClassReference.uploadProfilePictureToFirebase(f.name, Uri.fromFile(f),requireActivity())
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent(s: String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
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
                    try {
                        if (s.equals("report",false))
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        else if (s.equals("image",false))
                            startActivityForResult(takePictureIntent, 2)
                    }catch (e : Exception){
                        Toast.makeText(activity,R.string.camera_error,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initializeValues(view: View) {
        reference = FirebasePresenter(requireView())
        uploadClassReference = PatientUploadClass(view)
    }
}