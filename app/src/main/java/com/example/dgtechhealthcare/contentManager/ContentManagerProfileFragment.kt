package com.example.dgtechhealthcare.contentManager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.editProfile.EditContentManagerProfileFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ContentManagerProfileFragment : Fragment() {

    lateinit var name: TextView
    lateinit var contact : TextView
    lateinit var loc : TextView
    lateinit var email : TextView
    lateinit var editB : ImageView
    lateinit var image : ImageView

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

        image.setOnClickListener {
            val gallery : Intent = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            startActivityForResult(gallery,galleryPick)
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
        }
    }

    fun uploadToStorage(reference: FirebasePresenter, currentUserId: String?, imgUri: Uri, activity: FragmentActivity) {
        val resultUri = imgUri
        val path = reference.userProfileImgRef.child("$currentUserId.jpg")
        path.putFile(resultUri).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(activity,"Profile image changed", Toast.LENGTH_SHORT).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(currentUserId!!).child("profileImage").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful) Toast.makeText(activity,"Image stored", Toast.LENGTH_SHORT).show()
                    }
                }
            } else Toast.makeText(activity,"Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
        }
        image.setImageURI(imgUri)
    }

    private fun initializedValues(view: View) {
        name = view.findViewById(R.id.cmName)
        contact = view.findViewById(R.id.cmContact)
        loc = view.findViewById(R.id.cmLocation)
        email = view.findViewById(R.id.cmEmail)
        editB = view.findViewById(R.id.cmEdit)
        image = view.findViewById(R.id.cmImage)

        reference = FirebasePresenter(view)
    }
}