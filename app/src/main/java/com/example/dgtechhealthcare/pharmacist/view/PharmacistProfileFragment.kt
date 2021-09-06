package com.example.dgtechhealthcare.pharmacist.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.pharmacist.model.PharmacistProfileData
import com.example.dgtechhealthcare.pharmacist.presenter.PharmacistPresenter
import com.example.dgtechhealthcare.pharmacist.presenter.UploadImagePresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter
import kotlinx.android.synthetic.main.fragment_pharmacist_profile.*
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class PharmacistProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var uploadReference : UploadImagePresenter
    lateinit var presenter : PharmacistPresenter

    lateinit var currentPhotoPath: String

    val REQUEST_IMAGE_CAPTURE = 1

    private val imagePick = 0
    lateinit var imageUri : Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pharmacist_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        val data = PharmacistProfileData(pharmacistIV,usernameTV,pharmacyNameTV,contactTV,locationTV)
        presenter.populateProfile(data,activity)

        editBV.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val frag = EditPharmacistFragment()
            transaction?.replace(R.id.fragment_container_pharmacist, frag)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        pharmacistCameraEdit.setOnClickListener {
            val options = arrayOf("Camera","Gallery","Cancel")
            val builder = AlertDialog.Builder(activity)
            val a = builder.create()
            builder.setCancelable(false)
            builder.setTitle("Take Picture from")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                if (which ==0){
                    dispatchTakePictureIntent("image")
                    a.cancel()
                }
                if (which == 1) {
                    val image = Intent().setAction(Intent.ACTION_GET_CONTENT)
                    image.setType("image/*")
                    startActivityForResult(image, imagePick)
                }
                if (which ==2){
                    a.dismiss()
                }
            })
            builder.show()
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
                        Toast.makeText(activity,R.string.camera_error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initializeValues(view: View) {
        reference = FirebasePresenter(view)
        presenter = PharmacistPresenter(view)
        uploadReference = UploadImagePresenter(view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imagePick && resultCode == Activity.RESULT_OK && data != null){
            imageUri = data.data!!
            uploadReference.uploadImageToStorage(imageUri,requireActivity())

            pharmacistIV.setImageURI(imageUri)
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val f = File(currentPhotoPath)
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                mediaScanIntent.data = Uri.fromFile(f)
                activity?.sendBroadcast(mediaScanIntent)
                uploadReference.uploadProfilePictureToFirebase(f.name, Uri.fromFile(f),requireActivity())
            }
        }
    }
}