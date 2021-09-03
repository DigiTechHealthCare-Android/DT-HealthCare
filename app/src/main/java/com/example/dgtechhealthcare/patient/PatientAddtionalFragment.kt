package com.example.dgtechhealthcare.patient

import android.app.Activity
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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.patient.model.PatientDataClass
import com.example.dgtechhealthcare.patient.presenter.PatientPresenter
import com.example.dgtechhealthcare.patient.presenter.PatientUploadClass
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.utils.ViewImageActivity
import com.example.dgtechhealthcare.utils.ViewPdfActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*

class PatientAddtionalFragment : Fragment() {

    lateinit var fname : TextView
    lateinit var mname : TextView
    lateinit var address : TextView
    lateinit var dname : TextView
    lateinit var hname : TextView
    lateinit var viewHistory : TextView
    lateinit var upload : Button

    lateinit var currentPhotoPath: String
    val REQUEST_IMAGE_CAPTURE = 1


    lateinit var reference : FirebasePresenter
    lateinit var uploadClass : PatientUploadClass
    lateinit var presenter : PatientPresenter

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
                                            val i = Intent(activity, ViewImageActivity::class.java)
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

        upload.setOnClickListener {
            val options = arrayOf<CharSequence>("Take from Camera","Upload from gallery","Cancel")
            val builder : AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle("Do you want to?")
            builder.setItems(options,DialogInterface.OnClickListener { dialog, which ->
                if(which ==0 ){
                    dispatchTakePictureIntent()
                }else {
                    val gallery : Intent = Intent()
                    gallery.setAction(Intent.ACTION_GET_CONTENT)
                    gallery.setType("*/*")
                    startActivityForResult(gallery,galleryPick)
                }
            })
            builder.show()
        }

        val data = PatientDataClass(fname,mname,address,dname,hname,upload)
        presenter.populateAdditionalInfo(userType,data)
    }

    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(requireActivity(), "com.example.android.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.data!!
            uploadClass.uploadHistoryToStorage(imgUri,requireActivity())
        } else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val f : File = File(currentPhotoPath)
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                mediaScanIntent.data = Uri.fromFile(f)
                activity?.sendBroadcast(mediaScanIntent)
                uploadClass.uploadHistoryToStorage(Uri.fromFile(f),requireActivity())
            }
        }
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
        uploadClass = PatientUploadClass(view)
        presenter = PatientPresenter(view)
    }
}