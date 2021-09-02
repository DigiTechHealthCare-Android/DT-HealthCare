package com.example.dgtechhealthcare.contentManager.model

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.contentManager.EditArticlesFragment
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.utils.ViewPdfActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.regex.Pattern

class ArticleDetailsModel(view : View) {

    val reference = FirebasePresenter(view)

    fun editArticle(view: View, requireActivity: FragmentActivity, articleID: String,
        cEdit: ImageView) {
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("accountType").value.toString().compareTo("patient")==0){
                    cEdit.visibility = View.INVISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        cEdit.setOnClickListener {
            val options = arrayOf("Edit","Delete")
            val builder = AlertDialog.Builder(requireActivity)
            builder.setTitle("Do you want to?")
            builder.setItems(options,
                DialogInterface.OnClickListener { dialog, which ->
                    if(which == 0){ editContent(articleID,requireActivity) }
                    if(which ==1){ deleteContent(articleID,requireActivity) }
                })
            builder.show()
        }
    }

    private fun deleteContent(userID: String?,activity: FragmentActivity) {
        reference.articleReference.child(userID!!).removeValue()
        reference.managerReference.child(reference.currentUserId!!).child("articles").child(
            userID).removeValue().addOnCompleteListener {
            if(it.isSuccessful) Toast.makeText(activity,"Article Deleted", Toast.LENGTH_LONG).show()
        }
        activity?.supportFragmentManager?.popBackStack()
    }
    private fun editContent(userID: String?,activity: FragmentActivity) {
        val i = Intent(activity,EditArticlesFragment::class.java)
        i.putExtra("userID",userID)
        activity.startActivity(i)
        /*val frag = EditArticlesFragment()
        val bundle = Bundle()
        bundle.putString("userID",userID)
        frag.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.contentShowCV,frag)?.addToBackStack(null)?.commit()*/
    }

    fun populateArtcile(articleID: String, cTitle: TextView, userName: TextView,
        articleViews: TextView, cDesc: TextView, type: String, cImg: ImageView,
        userImage: ImageView, requireActivity: FragmentActivity) {
        reference.articleReference.child(articleID).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val title = snapshot.child("title").value.toString()
                cTitle.setText(title)
                Picasso.get().load(snapshot.child("publisherImage").value.toString()).into(userImage)
                userName.setText(snapshot.child("publisherName").value.toString())

                val views = snapshot.child("views").value.toString()
                articleViews.setText("$views" + " views")

                val desc = snapshot.child("desc").value.toString()
                if (desc.isNullOrEmpty()){
                    cDesc.setText("No description found.")
                } else {
                    cDesc.setText(desc)
                }


                if(type.compareTo("image")==0){
                    Glide.with(requireActivity)
                        .load(snapshot.child("imageRef").value.toString())
                        .placeholder(R.drawable.loading1).into(cImg)
                    //Picasso.get().load(snapshot.child("imageRef").value.toString()).into(cImg)
                    cDesc.setText(snapshot.child("desc").value.toString())
                } else if(type.compareTo("research")==0){
                    Picasso.get().load(R.drawable.researchdemoimg).into(cImg)
                    //Glide.with(requireActivity).load(R.drawable.researchdemoimg).into(cImg)


                    var url = ""
                    if(snapshot.hasChild("researchRef")) {
                        url = snapshot.child("researchRef").value.toString()
                        cImg.setOnClickListener {
                            val i = Intent(requireActivity, ViewPdfActivity::class.java)
                            i.putExtra("url",url)
                            requireActivity.startActivity(i)
                        }
                    } else if(snapshot.hasChild("url")){
                        url = snapshot.child("url").value.toString()
                        cImg.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            requireActivity.startActivity(intent)
                        }

                    }
                } else if(type.compareTo("video")==0){
                    val url = snapshot.child("url").value.toString()
                    var vid = ""
                    val pattern = Pattern.compile("^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$", Pattern.CASE_INSENSITIVE)
                    val matcher = pattern.matcher(url)
                    if (matcher.matches()){ vid = matcher.group(1)}

                    val player : YouTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance()

                    player.initialize("AIzaSyB9O8Gvi7gpkfsI2gKIImhVDnZODsTYiK4",object : YouTubePlayer.OnInitializedListener{
                        override fun onInitializationSuccess(
                            p0: YouTubePlayer.Provider?,
                            p1: YouTubePlayer?,
                            p2: Boolean
                        ) {
                            if(!p2){
                                var p : YouTubePlayer = p1!!
                                p.setFullscreen(false)
                                p.loadVideo(vid)
                                //p.play()
                            }
                        }

                        override fun onInitializationFailure(p0: YouTubePlayer.Provider?,
                                                             p1: YouTubeInitializationResult?) {}
                    })
                    val transaction : FragmentTransaction = requireActivity.supportFragmentManager.beginTransaction()
                    transaction.add(R.id.abc,player as Fragment).commit()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}