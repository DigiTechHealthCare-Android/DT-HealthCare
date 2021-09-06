package com.example.dgtechhealthcare.contentManager

import android.app.Activity.RESULT_OK
import android.content.Context
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
import com.example.dgtechhealthcare.contentManager.contract.AddContentContract
import com.example.dgtechhealthcare.contentManager.presenter.AddContentPresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter
import kotlinx.android.synthetic.main.fragment_add_content.*
import java.util.*

class AddContentFragment : Fragment(), AddContentContract.View {

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
        contentAddImage.visibility = View.GONE
        researchRG.visibility = View.GONE
        contentUrl.visibility = View.GONE
        uploadResearchB.visibility = View.GONE

        contentAddImage.setOnClickListener {
            val gallery = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            startActivityForResult(gallery,galleryPick)
        }

        contentRG.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.contentImageR -> {
                    contentAddImage.visibility = View.VISIBLE
                    contentAddDesc.visibility = View.VISIBLE
                    researchRG.visibility = View.GONE
                    contentUrl.visibility = View.GONE
                    uploadResearchB.visibility = View.GONE
                    type = "image"
                }
                R.id.contentVideoR -> {
                    type = "video"
                    contentAddImage.visibility = View.GONE
                    contentUrl.visibility = View.VISIBLE
                    researchRG.visibility = View.GONE
                    uploadResearchB.visibility = View.GONE
                }
                R.id.contentResearchR -> {
                    type = "research"
                    contentAddImage.visibility = View.GONE
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

        publishContent.setOnClickListener {
            val title = contentAddTitle.text.toString()
            val desc = contentAddDesc.text.toString()
            val url = contentUrl.text.toString()

            presenter.publishArticle(title,desc,url,type,contentUid,requireActivity(),uploadpdf)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == RESULT_OK && data!=null) {
            imgUri = data.data!!
            presenter.uploadImage(reference,reference.currentUserId!!,imgUri,requireActivity(),contentUid)
            contentAddImage.setImageURI(imgUri)
        } else if(requestCode == 1 && resultCode == RESULT_OK && data!= null) {
            imgUri = data.data!!
            presenter.uploadReportToDatabase(reference,reference.currentUserId!!,imgUri,requireActivity(),contentUid)
        }
    }

    private fun initializeValues(view: View) {
        reference = FirebasePresenter(view)
        presenter = AddContentPresenter(view)
    }

    override fun publishContentMessage(context: Context) {
        Toast.makeText(context,R.string.content_published, Toast.LENGTH_LONG).show()
    }

    override fun imageUploadMessage(context: Context) {
        Toast.makeText(context,R.string.image_uploaded,Toast.LENGTH_LONG).show()
    }
}