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
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.contentManager.contract.CMProfileContract
import com.example.dgtechhealthcare.contentManager.presenter.FileStoragePresenter
import com.example.dgtechhealthcare.editProfile.EditContentManagerProfileFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_content_manager_profile.*
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ContentManagerProfileFragment : Fragment(), CMProfileContract.View{

    lateinit var currentPhotoPath: String
    val REQUEST_IMAGE_CAPTURE = 1

    lateinit var reference : FirebasePresenter
    lateinit var filePresenter : FileStoragePresenter
    var galleryPick : Int = 0
    var imgUri : Uri = Uri.parse("")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_content_manager_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)
        filePresenter = FileStoragePresenter(view)

        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                cmName.setText(snapshot.child("username").value.toString())
                cmContact.setText(snapshot.child("contact").value.toString())
                cmLocation.setText(snapshot.child("location").value.toString())
                cmEmail.setText(snapshot.child("email").value.toString())
                Picasso.get().load(snapshot.child("profileImage").value.toString()).into(cmImage)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        managerCameraEdit.setOnClickListener {
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

        cmEdit.setOnClickListener {
            val frag = EditContentManagerProfileFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.CMProfileFrame,frag)?.addToBackStack(null)?.commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.data!!
            filePresenter.uploadToStorage(reference,reference.currentUserId,imgUri,requireActivity())
            cmImage.setImageURI(imgUri)
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val f = File(currentPhotoPath)
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                mediaScanIntent.data = Uri.fromFile(f)
                activity?.sendBroadcast(mediaScanIntent)
                filePresenter.uploadProfilePictureToFirebase(f.name, Uri.fromFile(f),requireActivity())
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

    override fun imageUploadMessage(context: Context) {
        Toast.makeText(context, R.string.image_uploaded, Toast.LENGTH_SHORT).show()
    }

    override fun errorMessage(context: Context, task: Task<UploadTask.TaskSnapshot>) {
        Toast.makeText(context,"Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
    }

    override fun profileImageMessage(context: Context) {
        Toast.makeText(context, R.string.profile_image, Toast.LENGTH_SHORT).show()
    }
}