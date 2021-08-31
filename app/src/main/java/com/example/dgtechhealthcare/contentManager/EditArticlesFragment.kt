package com.example.dgtechhealthcare.contentManager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.HashMap

class EditArticlesFragment : Fragment() {

    lateinit var title : EditText
    lateinit var contentImg : ImageView
    lateinit var contentDesc : EditText
    lateinit var contentUrl : EditText
    lateinit var contentRG : RadioGroup
    lateinit var publishB : Button

    lateinit var reference : FirebasePresenter
    var galleryPick : Int = 0
    var imgUri : Uri = Uri.parse("")

    var contentUid = ""
    var type1 = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}

        contentUid = arguments?.getString("userID","")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_articles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        reference.articleReference.child(contentUid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val type = snapshot.child("type").value.toString()
                contentRG.visibility = View.GONE
                contentImg.visibility = View.GONE
                for(i in 0..contentRG.childCount-1){
                    contentRG.getChildAt(i).isEnabled = false
                }
                if(type.compareTo("image")==0){
                    contentRG.check(R.id.contentImageRE)
                    contentImg.setImageURI(Uri.parse(snapshot.child("imageRef").value.toString()))
                } else if(type.compareTo("video")==0){
                    contentRG.check(R.id.contentVideoRE)
                } else if(type.compareTo("research")==0){
                    contentRG.check(R.id.contentResearchRE)
                    //contentImg.setImageURI(Uri.parse(snapshot.child("imageRef").value.toString()))
                }
                title.setText(snapshot.child("title").value.toString())
                contentDesc.setText(snapshot.child("desc").value.toString())
                contentUrl.setText(snapshot.child("url").value.toString())
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        publishB.setOnClickListener {
            val hm = HashMap<String,Any>()
            hm["title"]= title.text.toString()
            hm["desc"]= contentDesc.text.toString()
            hm["url"] = contentUrl.text.toString()
            reference.articleReference.child(contentUid).updateChildren(hm).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(activity,"Article Updated",Toast.LENGTH_LONG).show()
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        }

        contentImg.setOnClickListener {
            val gallery = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            startActivityForResult(gallery,galleryPick)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == Activity.RESULT_OK && data!=null) {
            imgUri = data.data!!
            uploadImageToDatabase(reference,contentUid,imgUri)
        }
    }

    private fun uploadImageToDatabase(reference: FirebasePresenter, contentUid: String, imgUri: Uri) {
        val resultUri = imgUri
        val path = reference.contentPostRef.child(contentUid + ".jpg")
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
        title = view.findViewById(R.id.contentEditTitle)
        contentImg = view.findViewById(R.id.contentEditImage)
        contentDesc = view.findViewById(R.id.contentEditDesc)
        publishB = view.findViewById(R.id.publishEditContent)
        contentUrl = view.findViewById(R.id.contentEditUrl)
        contentRG = view.findViewById(R.id.contentEditRG)

        reference = FirebasePresenter(view)
    }
}