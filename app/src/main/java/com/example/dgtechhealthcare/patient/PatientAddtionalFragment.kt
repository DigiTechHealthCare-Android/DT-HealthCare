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
import com.example.dgtechhealthcare.patient.presenter.PatientViewReportPresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.utils.ViewImageActivity
import com.example.dgtechhealthcare.utils.ViewPdfActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_patient_addtional.*
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
            PatientViewReportPresenter().showReport(reference,userType,requireActivity(),requireContext(),"medicalHistory")
        }

        uploadHistory.setOnClickListener {
            val options = arrayOf<CharSequence>("Take from Camera","Upload from gallery","Cancel")
            val builder : AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle("Do you want to?")
            builder.setItems(options,DialogInterface.OnClickListener { dialog, which ->
                if(which ==0 ){
                    dispatchTakePictureIntent()
                }else if (which == 1){
                    val gallery : Intent = Intent()
                    gallery.setAction(Intent.ACTION_GET_CONTENT)
                    gallery.setType("*/*")
                    startActivityForResult(gallery,galleryPick)
                }
            })
            builder.show()
        }

        val data = PatientDataClass(addFName,addMName,addAddress,addDoctorName,addHospitalName,uploadHistory)
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
        reference = FirebasePresenter(view)
        uploadClass = PatientUploadClass(view)
        presenter = PatientPresenter(view)
    }
}