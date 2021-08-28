package com.example.dgtechhealthcare.contentManager

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_add_content.*
import java.sql.Time
import java.util.*
import kotlin.collections.HashMap

class AddContentFragment : Fragment() {

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
    var type = ""

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
        contentImg.visibility = View.GONE

        contentImg.setOnClickListener {
            val gallery = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            startActivityForResult(gallery,galleryPick)
        }

        contentRG.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.contentImageR -> {
                    contentImg.visibility = View.VISIBLE
                    contentDesc.visibility = View.VISIBLE
                    type = "image"
                }
                R.id.contentVideoR -> {
                    type = "video"
                    contentImg.visibility = View.GONE
                }
                R.id.contentResearchR -> {
                    type = "research"
                    contentImg.visibility = View.VISIBLE
                }
            }
        }

        publishB.setOnClickListener {

            reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val img = snapshot.child("profileImage").value.toString()
                    val username = snapshot.child("username").value.toString()

                    val hm = HashMap<String,Any>()
                    hm["publisherName"] = username
                    hm["publisherImage"] = img
                    hm["title"] = title.text.toString()
                    hm["desc"] = contentDesc.text.toString()
                    hm["url"] = contentUrl.text.toString()
                    hm["time"] = Calendar.getInstance().time.toString()
                    hm["type"] = type
                    hm["views"] = 0
                    if(type.compareTo("video")==0 || type.compareTo("research")==0){
                        if (contentUrl.text.toString().isNullOrEmpty()){
                            Toast.makeText(activity,"Youtube Video link required",Toast.LENGTH_LONG).show()
                        }else {
                            reference.articleReference.child(contentUid.toString()).updateChildren(hm)
                                .addOnCompleteListener {
                                    if(it.isSuccessful){
                                        val h = HashMap<String,Any>()
                                        h["contentUid"] = contentUid
                                        h["time"] = Calendar.getInstance().time.toString()
                                        reference.managerReference.child(reference.currentUserId!!).child("articles").child(contentUid).updateChildren(h).addOnCompleteListener {
                                            Toast.makeText(activity,"Content published",Toast.LENGTH_LONG).show()
                                            activity?.supportFragmentManager?.popBackStack()
                                        }
                                    }
                                }
                        }
                    }
                    if (type.compareTo("image")==0){
                        reference.articleReference.child(contentUid.toString()).updateChildren(hm)
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    val h = HashMap<String,Any>()
                                    h["contentUid"] = contentUid
                                    h["time"] = Calendar.getInstance().time.toString()
                                    reference.managerReference.child(reference.currentUserId!!).child("articles").child(contentUid).updateChildren(h).addOnCompleteListener {
                                        Toast.makeText(activity,"Content published",Toast.LENGTH_LONG).show()
                                        activity?.supportFragmentManager?.popBackStack()
                                    }
                                }
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == RESULT_OK && data!=null) {
            imgUri = data.data!!
            uploadImageToDatabase(reference,reference.currentUserId,imgUri,requireActivity())
        }
    }

    private fun uploadImageToDatabase(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri,
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
        contentRG = view.findViewById(R.id.contentRG)

        reference = FirebasePresenter(view)
    }
}