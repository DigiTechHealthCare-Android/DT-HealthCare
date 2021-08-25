package com.example.dgtechhealthcare.contentManager

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ContentMangerShowContentFragment(val view: View) {

    val reference = FirebasePresenter(view)

    fun displayContent(recyclerView: RecyclerView,activity: FragmentActivity){

        val options = FirebaseRecyclerOptions.Builder<ContentDataClass>()
            .setQuery(reference.articleReference, ContentDataClass::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<ContentDataClass,ContentViewHolder> =
            object : FirebaseRecyclerAdapter<ContentDataClass,ContentViewHolder>(options){
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_content_manger_show_content,parent,false)
                    return ContentViewHolder(view)
                }
                override fun onBindViewHolder(holder: ContentViewHolder,
                    position: Int, model: ContentDataClass) {

                    val userID = getRef(position).key

                    holder.edit.setOnClickListener {
                        val options = arrayOf("Edit","Delete")
                        val builder = AlertDialog.Builder(activity)
                        builder.setTitle("Do you want to?")
                        builder.setItems(options,DialogInterface.OnClickListener { dialog, which ->
                            if(which == 0){
                                editContent(userID)
                            }
                            if(which ==1){
                                deleteContent(userID)
                            }
                        })
                        builder.show()
                    }


                    reference.articleReference.child(userID!!).addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.hasChild("type")){
                                if(snapshot.child("type").value.toString().compareTo("video")==0){
                                    val title = snapshot.child("title").value.toString()
                                    val url = snapshot.child("url").value.toString()

                                    holder.title.setText(title)

                                    var yplayer : YouTubePlayer? = null
                                    val player : YouTubePlayerSupportFragment = activity.supportFragmentManager.findFragmentById(R.id.abc) as YouTubePlayerSupportFragment
                                    player.initialize("AIzaSyB9O8Gvi7gpkfsI2gKIImhVDnZODsTYiK4",object : YouTubePlayer.OnInitializedListener{
                                        override fun onInitializationSuccess(
                                            p0: YouTubePlayer.Provider?,
                                            p1: YouTubePlayer?,
                                            p2: Boolean
                                        ) {
                                            if(!p2){
                                                yplayer = p1
                                                yplayer?.setFullscreen(false)
                                                yplayer?.loadVideo("HyHNuVaZJ-k")
                                            }
                                        }

                                        override fun onInitializationFailure(
                                            p0: YouTubePlayer.Provider?,
                                            p1: YouTubeInitializationResult?
                                        ) {}

                                    })
                                } else if(snapshot.child("type").value.toString().compareTo("research")==0){
                                    val title = snapshot.child("title").value.toString()
                                    val imageRef = snapshot.child("imageRef").value.toString()
                                    val url = snapshot.child("url").value.toString()

                                    holder.title.setText(title)
                                    Picasso.get().load(imageRef).into(holder.image)

                                    holder.cardView.setOnClickListener {
                                        val uri = Uri.parse(url)
                                        val intent = Intent(Intent.ACTION_VIEW,uri)
                                        it.context.startActivity(intent)
                                    }

                                }
                            }else {
                                val title = snapshot.child("title").value.toString()
                                val url = snapshot.child("url").value.toString()
                                val imageRef = snapshot.child("imageRef").value.toString()
                                val desc = snapshot.child("desc").value.toString()

                                holder.title.setText(title)
                                holder.desc.setText(desc)
                                Picasso.get().load(imageRef).into(holder.image)

                                holder.cardView.setOnClickListener {
                                    val uri = Uri.parse("https:///www.youtube.com")
                                    val intent = Intent(Intent.ACTION_VIEW,uri)
                                    it.context.startActivity(intent)
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }

                private fun deleteContent(userID: String?) {
                    reference.articleReference.child(userID!!).removeValue().addOnCompleteListener {
                        if(it.isSuccessful){
                            reference.managerReference.child(reference.currentUserId!!).child("articles").child(userID!!).removeValue().addOnCompleteListener {
                                if(it.isSuccessful) Toast.makeText(activity,"Article Deleted",Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                private fun editContent(userID: String?) {
                    val frag = EditArticlesFragment()
                    val bundle = Bundle()
                    bundle.putString("userID",userID)
                    frag.arguments = bundle
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.contentFrame,frag).addToBackStack(null).commit()
                }

            }

        activity.registerForContextMenu(recyclerView)
        recyclerView.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()

    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.contentShowTitle)
        val desc = itemView.findViewById<TextView>(R.id.contentShowDesc)
        val image = itemView.findViewById<ImageView>(R.id.contentShowImg)
        val cardView = itemView.findViewById<CardView>(R.id.contentShowCV)
        val edit = itemView.findViewById<ImageButton>(R.id.contentEdit)
        //val video = itemView.findViewById<FrameLayout>(R.id.youtube_fragment)
    }
}