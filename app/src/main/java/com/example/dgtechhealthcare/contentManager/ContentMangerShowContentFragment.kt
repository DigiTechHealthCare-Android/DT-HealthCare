package com.example.dgtechhealthcare.contentManager

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.patientInfo.PatientInfoDataClass
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.android.youtube.player.YouTubePlayerView
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

                                    /*var yplayer : YouTubePlayer? = null

                                    val player = YouTubePlayerSupportFragment.newInstance()
                                    val transaction : FragmentTransaction = activity.supportFragmentManager.beginTransaction()
                                    transaction.add(R.id.youtube_fragment,player).commit()
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
                                                yplayer?.play()
                                            }
                                        }

                                        override fun onInitializationFailure(
                                            p0: YouTubePlayer.Provider?,
                                            p1: YouTubeInitializationResult?
                                        ) {}

                                    })*/
                                    //val frag : YouTubePlayerSupportFragment = activity.supportFragmentManager.findFragmentById(R.id.youtube_player_fragment)



                                    /*holder.video.initialize("AIzaSyB9O8Gvi7gpkfsI2gKIImhVDnZODsTYiK4",object : YouTubePlayer.OnInitializedListener{

                                        override fun onInitializationSuccess(p0: YouTubePlayer.Provider?,
                                            p1: YouTubePlayer?, p2: Boolean) {
                                            p1?.cueVideo("HyHNuVaZJ-k")
                                        }

                                        override fun onInitializationFailure(p0: YouTubePlayer.Provider?,
                                            p1: YouTubeInitializationResult?) {

                                        }

                                    })*/
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
        //val video = itemView.findViewById<FrameLayout>(R.id.youtube_fragment)
    }
}