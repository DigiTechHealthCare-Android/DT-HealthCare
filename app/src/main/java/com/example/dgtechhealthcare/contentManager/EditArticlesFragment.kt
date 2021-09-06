package com.example.dgtechhealthcare.contentManager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.view.ContentManagerDrawerNavigationActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_edit_articles.*
import kotlin.collections.HashMap

class EditArticlesFragment : AppCompatActivity() {

    lateinit var reference: FirebasePresenter
    var galleryPick: Int = 0
    var imgUri: Uri = Uri.parse("")

    var contentUid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_edit_articles)
        contentUid = intent.getStringExtra("userID")!!

        initializeValues()

        reference.articleReference.child(contentUid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val type = snapshot.child("type").value.toString()
                contentEditRG.visibility = View.GONE
                contentEditImage.visibility = View.GONE
                for(i in 0..contentEditRG.childCount-1){
                    contentEditRG.getChildAt(i).isEnabled = false
                }
                if(type.compareTo("image")==0){
                    contentEditRG.check(R.id.contentImageRE)
                    contentEditImage.setImageURI(Uri.parse(snapshot.child("imageRef").value.toString()))
                } else if(type.compareTo("video")==0){
                    contentEditRG.check(R.id.contentVideoRE)
                } else if(type.compareTo("research")==0){
                    contentEditRG.check(R.id.contentResearchRE)
                    contentEditUrl.visibility = View.GONE
                    val t = findViewById<TextView>(R.id.textView84)
                    t.visibility = View.GONE
                }
                contentEditTitle.setText(snapshot.child("title").value.toString())
                contentEditDesc.setText(snapshot.child("desc").value.toString())
                contentEditUrl.setText(snapshot.child("url").value.toString())
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        publishEditContent.setOnClickListener {
            val hm = HashMap<String,Any>()
            hm["title"]= contentEditTitle.text.toString()
            hm["desc"]= contentEditDesc.text.toString()
            hm["url"] = contentEditUrl.text.toString()
            reference.articleReference.child(contentUid).updateChildren(hm).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this,R.string.article_update,Toast.LENGTH_LONG).show()
                    //val i = Intent(this,ContentManagerDrawerNavigationActivity::class.java)
                    //startActivity(i)
                    finish()
                }
            }
        }

        contentEditImage.setOnClickListener {
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

    fun uploadImageToDatabase(reference: FirebasePresenter, contentUid: String, imgUri: Uri) {
        val resultUri = imgUri
        val path = reference.contentPostRef.child(contentUid + ".jpg")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful){
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.articleReference.child(contentUid).child("imageRef").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(this,R.string.image_uploaded,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        contentEditImage.setImageURI(imgUri)
    }

    fun initializeValues() {
        reference = FirebasePresenter(View(this))
    }
}