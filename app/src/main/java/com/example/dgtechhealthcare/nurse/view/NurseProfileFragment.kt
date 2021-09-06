package com.example.dgtechhealthcare.nurse.view

import android.app.Activity
import android.app.Activity.RESULT_OK
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
import android.widget.*
import androidx.core.content.FileProvider
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.nurse.model.ProfileData
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class NurseProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter

    lateinit var nurseProfileImg : ImageView
    lateinit var nameTextView : TextView
    lateinit var mobileTextView : TextView
    lateinit var hospitalNameTextView : TextView
    lateinit var emailTextView: TextView
    lateinit var dobTextView: TextView
    lateinit var genderTextView: TextView
    lateinit var editButton: ImageView
    lateinit var cameraEdit : ImageView

    lateinit var currentPhotoPath: String

    val REQUEST_IMAGE_CAPTURE = 1

    private val imagePick = 0
    lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nurse_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)

        nurseProfileImg = view.findViewById(R.id.nurseIV)
        nameTextView = view.findViewById(R.id.nurseNameTV)
        mobileTextView = view.findViewById(R.id.nurseContactTV)
        hospitalNameTextView = view.findViewById(R.id.hospitalNameTV)
        emailTextView = view.findViewById(R.id.emailTV)
        dobTextView = view.findViewById(R.id.birthTV)
        genderTextView = view.findViewById(R.id.genderTV)
        editButton = view.findViewById(R.id.editB)
        cameraEdit = view.findViewById(R.id.nurseCameraEdit)

        editButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val frag = EditNurseProfileFragment()
            transaction?.replace(R.id.fragment_container_nurse, frag)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        cameraEdit.setOnClickListener {
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

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nurseData = snapshot.getValue(ProfileData::class.java)

                if (nurseData?.profileImage == null){
                    nurseProfileImg.setImageResource(R.drawable.profile)
                }
                else{
                    Picasso.get().load(nurseData?.profileImage).into(nurseProfileImg)
                }

                nameTextView.text = nurseData?.username
                mobileTextView.text = "${nurseData?.contact}"
                hospitalNameTextView.text = "${nurseData?.hospital}"
                emailTextView.text = "${nurseData?.email}"
                dobTextView.text = "${nurseData?.dateOfBirth}"
                genderTextView.text = "${nurseData?.gender}"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun uploadProfilePictureToFirebase(f: String,uri: Uri,activity: Context){
        val path = reference.userProfileImgRef.child("${f}.pdf")
        path.putFile(uri).addOnCompleteListener {
            if(it.isSuccessful) {
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(reference.currentUserId!!).child("profileImage").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,R.string.image_uploaded,Toast.LENGTH_SHORT).show()
                    }
                }
            }else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imagePick && resultCode == RESULT_OK && data != null){
            imageUri = data.data!!
            uploadImageToStorage()
            nurseProfileImg.setImageURI(imageUri)
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val f = File(currentPhotoPath)
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                mediaScanIntent.data = Uri.fromFile(f)
                activity?.sendBroadcast(mediaScanIntent)
                uploadProfilePictureToFirebase(f.name, Uri.fromFile(f),requireActivity())
            }
        }
    }

    private fun uploadImageToStorage() {
        val imgPath = reference.userProfileImgRef.child("${reference.currentUserId}.jpg")

        imgPath.putFile(imageUri).addOnSuccessListener {
            imgPath.downloadUrl.addOnSuccessListener {
                val downloadUri = it.toString()
                // save Image to Firebase Realtime Database
                reference.userReference.child(reference.currentUserId!!).child("profileImage").setValue(downloadUri)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this.activity, R.string.image_uploaded, Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
}