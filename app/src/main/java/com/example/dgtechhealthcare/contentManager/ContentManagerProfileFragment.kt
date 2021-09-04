package com.example.dgtechhealthcare.contentManager

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
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.editProfile.EditContentManagerProfileFragment
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

class ContentManagerProfileFragment : Fragment() {

    lateinit var name: TextView
    lateinit var contact : TextView
    lateinit var loc : TextView
    lateinit var email : TextView
    lateinit var editB : ImageView
    lateinit var image : ImageView
    lateinit var cameraEdit : ImageView

    lateinit var currentPhotoPath: String

    val REQUEST_IMAGE_CAPTURE = 1

    lateinit var reference : FirebasePresenter
    var galleryPick : Int = 0
    var imgUri : Uri = Uri.parse("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_content_manager_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializedValues(view)

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                name.setText(snapshot.child("username").value.toString())
                contact.setText(snapshot.child("contact").value.toString())
                loc.setText(snapshot.child("location").value.toString())
                email.setText(snapshot.child("email").value.toString())
                Picasso.get().load(snapshot.child("profileImage").value.toString()).into(image)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

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
                    val gallery : Intent = Intent()
                    gallery.setAction(Intent.ACTION_GET_CONTENT)
                    gallery.setType("image/*")
                    startActivityForResult(gallery,galleryPick)
                }
                if (which ==2){
                    a.dismiss()
                }
            })
            builder.show()
        }

        editB.setOnClickListener {
            val frag = EditContentManagerProfileFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.CMProfileFrame,frag)?.addToBackStack(null)?.commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.data!!
            uploadToStorage(reference,reference.currentUserId,imgUri,requireActivity())
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val f = File(currentPhotoPath)
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                mediaScanIntent.data = Uri.fromFile(f)
                activity?.sendBroadcast(mediaScanIntent)
                uploadProfilePictureToFirebase(f.name, Uri.fromFile(f),requireActivity())
            }
        }
    }

    fun uploadProfilePictureToFirebase(f: String,uri: Uri,activity: Context){
        val path = reference.userProfileImgRef.child("${f}.pdf")
        path.putFile(uri).addOnCompleteListener {
            if(it.isSuccessful) {
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(reference.currentUserId!!).child("profileImage").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,"Image Uploaded",Toast.LENGTH_SHORT).show()
                    }
                }
            }else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
        }
    }

    fun uploadToStorage(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri, activity: FragmentActivity) {
        val resultUri = imgUri
        val path = reference.userProfileImgRef.child("$currentUserId.jpg")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(activity,R.string.profile_image, Toast.LENGTH_SHORT).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(currentUserId!!).child("profileImage").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,R.string.image_uploaded, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        image.setImageURI(imgUri)
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
                        Toast.makeText(activity,"Camera Error",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initializedValues(view: View) {
        name = view.findViewById(R.id.cmName)
        contact = view.findViewById(R.id.cmContact)
        loc = view.findViewById(R.id.cmLocation)
        email = view.findViewById(R.id.cmEmail)
        editB = view.findViewById(R.id.cmEdit)
        image = view.findViewById(R.id.cmImage)
        cameraEdit = view.findViewById(R.id.managerCameraEdit)

        reference = FirebasePresenter(view)
    }
}