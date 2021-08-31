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
import com.example.dgtechhealthcare.contentManager.presenter.AddContentPresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter
import java.util.*

class AddContentFragment : Fragment() {

    lateinit var title : EditText
    lateinit var contentImg : ImageView
    lateinit var contentDesc : EditText
    lateinit var contentUrl : EditText
    lateinit var contentRG : RadioGroup
    lateinit var researchRG : RadioGroup
    lateinit var uploadResearchB : Button
    lateinit var publishB : Button

    lateinit var reference : FirebasePresenter
    lateinit var presenter : AddContentPresenter
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

        var uploadpdf = false

        initializeValues(view)

        contentUid = UUID.randomUUID().toString()
        contentImg.visibility = View.GONE
        researchRG.visibility = View.GONE
        contentUrl.visibility = View.GONE
        uploadResearchB.visibility = View.GONE

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
                    researchRG.visibility = View.GONE
                    contentUrl.visibility = View.GONE
                    uploadResearchB.visibility = View.GONE
                    type = "image"
                }
                R.id.contentVideoR -> {
                    type = "video"
                    contentImg.visibility = View.GONE
                    contentUrl.visibility = View.VISIBLE
                    researchRG.visibility = View.GONE
                    uploadResearchB.visibility = View.GONE
                }
                R.id.contentResearchR -> {
                    type = "research"
                    contentImg.visibility = View.GONE
                    researchRG.visibility = View.VISIBLE
                    contentUrl.visibility = View.GONE
                }
            }
        }

        researchRG.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.storageRB -> {
                    uploadResearchB.visibility = View.VISIBLE
                    contentUrl.visibility = View.GONE
                    uploadpdf = true
                }
                R.id.urlRB -> {
                    contentUrl.visibility = View.VISIBLE
                    uploadResearchB.visibility = View.GONE
                    uploadpdf = false
                }
            }
        }

        uploadResearchB.setOnClickListener {
            val gallery = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("*/*")
            startActivityForResult(gallery,1)
        }

        publishB.setOnClickListener {
            val title = title.text.toString()
            val desc = contentDesc.text.toString()
            val url = contentUrl.text.toString()

            presenter.publishArticle(title,desc,url,type,contentUid,requireActivity(),uploadpdf)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == RESULT_OK && data!=null) {
            imgUri = data.data!!
            presenter.uploadImage(reference,reference.currentUserId!!,imgUri,requireActivity(),contentUid)
            contentImg.setImageURI(imgUri)
        } else if(requestCode == 1 && resultCode == RESULT_OK && data!= null) {
            imgUri = data.data!!
            uploadImageToDatabase(reference,reference.currentUserId!!,imgUri,requireActivity(),contentUid)
        }
    }

    fun uploadImageToDatabase(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri,
                              requireActivity: FragmentActivity, contentUid: String) {

        val resultUri = imgUri
        val time = Calendar.getInstance().time.toString()
        val path = reference.contentPostRef.child(currentUserId + time + "RESEARCH")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful){
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.articleReference.child(contentUid).child("researchRef").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(requireActivity,"$downloadUrl",Toast.LENGTH_LONG).show()
                    }
                }
            } else Toast.makeText(requireActivity,"Error: ${it.exception?.message}",Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeValues(view: View) {
        title = view.findViewById(R.id.contentAddTitle)
        contentImg = view.findViewById(R.id.contentAddImage)
        contentDesc = view.findViewById(R.id.contentAddDesc)
        publishB = view.findViewById(R.id.publishContent)
        contentUrl = view.findViewById(R.id.contentUrl)
        contentRG = view.findViewById(R.id.contentRG)
        researchRG = view.findViewById(R.id.researchRG)
        uploadResearchB = view.findViewById(R.id.uploadResearchB)

        reference = FirebasePresenter(view)
        presenter = AddContentPresenter(view)
    }
}