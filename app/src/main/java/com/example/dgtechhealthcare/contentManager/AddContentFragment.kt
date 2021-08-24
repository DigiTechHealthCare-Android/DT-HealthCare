package com.example.dgtechhealthcare.contentManager

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import java.sql.Time
import java.util.*
import kotlin.collections.HashMap

class AddContentFragment : Fragment() {

    lateinit var title : EditText
    lateinit var contentImg : ImageView
    lateinit var contentDesc : EditText
    lateinit var contentUrl : EditText
    lateinit var publishB : Button

    lateinit var reference : FirebasePresenter
    var galleryPick : Int = 0
    var imgUri : Uri = Uri.parse("")

    var contentUid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        contentUid = UUID.randomUUID().toString()

        contentImg.setOnClickListener {
            val gallery = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            startActivityForResult(gallery,galleryPick)
        }

        publishB.setOnClickListener {
            val hm = HashMap<String,Any>()
            hm["title"] = title.text.toString()
            hm["desc"] = contentDesc.text.toString()
            hm["url"] = contentUrl.text.toString()
            hm["time"] = Calendar.getInstance().time.toString()
            reference.articleReference.child(contentUid.toString()).updateChildren(hm)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val h = HashMap<String,Any>()
                        h["contentUid"] = contentUid
                        h["time"] = Calendar.getInstance().time.toString()
                        reference.managerReference.child(reference.currentUserId!!).child("articles").child(contentUid).updateChildren(h).addOnCompleteListener {
                            Toast.makeText(activity,"Content published",Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == RESULT_OK && data!=null) {
            imgUri = data.data!!
            uploadImageToDatabase(reference,reference.currentUserId,imgUri,requireActivity())
        }
    }

    private fun uploadImageToDatabase(reference: FirebasePresenter,
        currentUserId: String?, imgUri: Uri,
        requireActivity: FragmentActivity) {

        val resultUri = imgUri
        val time = Calendar.getInstance().time.toString()
        val path = reference.contentPostRef.child(currentUserId + time + ".jpg")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful){
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.articleReference.child(contentUid).child("imageRef").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,"$downloadUrl",Toast.LENGTH_LONG).show()
                    }
                }
            } else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_LONG).show()
        }
        contentImg.setImageURI(imgUri)
    }

    private fun initializeValues(view: View) {
        title = view.findViewById(R.id.contentAddTitle)
        contentImg = view.findViewById(R.id.contentAddImage)
        contentDesc = view.findViewById(R.id.contentAddDesc)
        publishB = view.findViewById(R.id.publishContent)
        contentUrl = view.findViewById(R.id.contentUrl)

        reference = FirebasePresenter(view)
    }
}