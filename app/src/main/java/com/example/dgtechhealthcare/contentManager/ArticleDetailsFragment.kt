package com.example.dgtechhealthcare.contentManager

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.regex.Pattern

class ArticleDetailsFragment : Fragment() {

    lateinit var reference : FirebasePresenter

    lateinit var cTitle : TextView
    lateinit var cImg : ImageView
    lateinit var cEdit : ImageView
    lateinit var cDesc : TextView
    lateinit var userImage : ImageView
    lateinit var userName : TextView
    lateinit var articleViews : TextView

    var articleID = ""
    var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articleID = arguments?.getString("articleID","")!!
        type = arguments?.getString("type","")!!

        initializeValues(view)

        editArticle(view,requireActivity(),articleID)
        populateArtcile(view,requireActivity())
    }

    private fun editArticle(view: View, requireActivity: FragmentActivity, articleID: String) {
        reference.userReference.child(reference.currentUserId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("accountType").value.toString().compareTo("patient")==0){
                    cEdit.visibility = View.INVISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        cEdit.setOnClickListener {
            val options = arrayOf("Edit","Delete")
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Do you want to?")
            builder.setItems(options,
                DialogInterface.OnClickListener { dialog, which ->
                    if(which == 0){ editContent(articleID) }
                    if(which ==1){ deleteContent(articleID) }
                })
            builder.show()
        }
    }

    private fun deleteContent(userID: String?) {
        reference.articleReference.child(userID!!).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                reference.managerReference.child(reference.currentUserId!!).child("articles").child(userID!!).removeValue().addOnCompleteListener {
                    if(it.isSuccessful) Toast.makeText(activity,"Article Deleted",
                        Toast.LENGTH_LONG).show()
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        }
    }
    private fun editContent(userID: String?) {
        val frag = EditArticlesFragment()
        val bundle = Bundle()
        bundle.putString("userID",userID)
        frag.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.contentFrame,frag)?.addToBackStack(null)?.commit()
    }

    private fun populateArtcile(view: View,activity: FragmentActivity) {
        reference.articleReference.child(articleID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val title = snapshot.child("title").value.toString()
                cTitle.setText(title)
                Picasso.get().load(snapshot.child("publisherImage").value.toString()).into(userImage)
                userName.setText(snapshot.child("publisherName").value.toString())

                val views = snapshot.child("views").value.toString()
                val v : Int = views.toInt()
                articleViews.setText("$v" + " views")

                val desc = snapshot.child("desc").value.toString()
                if (desc.isNullOrEmpty()){
                    cDesc.setText("No description found.")
                } else {
                    cDesc.setText(desc)
                }


                if(type.compareTo("image")==0){
                    Picasso.get().load(snapshot.child("imageRef").value.toString()).into(cImg)
                    cDesc.setText(snapshot.child("desc").value.toString())
                } else if(type.compareTo("research")==0){
                    Picasso.get().load(snapshot.child("imageRef").value.toString()).into(cImg)
                    val url = snapshot.child("url").value.toString()
                    cImg.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW,Uri.parse(url))
                        startActivity(intent)
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
                                p.play()
                            }
                        }

                        override fun onInitializationFailure(p0: YouTubePlayer.Provider?,
                            p1: YouTubeInitializationResult?) {}
                    })
                    val transaction : FragmentTransaction = activity.supportFragmentManager.beginTransaction()
                    transaction.add(R.id.abc,player as Fragment).commit()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initializeValues(view: View) {
        cTitle = view.findViewById(R.id.contentShowTitle)
        cImg = view.findViewById(R.id.contentShowImg)
        cDesc = view.findViewById(R.id.contentShowDesc)
        cEdit = view.findViewById(R.id.contentEdit)
        userImage = view.findViewById(R.id.articlePImageView)
        userName = view.findViewById(R.id.articlePTextView)
        articleViews = view.findViewById(R.id.countViews)

        reference = FirebasePresenter(view)
    }


    /*fun displayContent(recyclerView: RecyclerView, activity: FragmentActivity, role:String){

        if(role.compareTo("contentManager")==0){
            val options = FirebaseRecyclerOptions.Builder<ManagerDataClass>()
                .setQuery(reference.managerReference.child(reference.currentUserId!!).child("articles"), ManagerDataClass::class.java).build()

            val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<ManagerDataClass, ContentViewHolder> =
                object : FirebaseRecyclerAdapter<ManagerDataClass, ContentViewHolder>(options){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
                        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_content_manger_show_content,parent,false)
                        return ContentViewHolder(view)
                    }
                    override fun onBindViewHolder(holder: ContentViewHolder, position: Int, model: ManagerDataClass) {

                        val userID = getRef(position).key
                        holder.edit.setOnClickListener {
                            val options = arrayOf("Edit","Delete")
                            val builder = AlertDialog.Builder(activity)
                            builder.setTitle("Do you want to?")
                            builder.setItems(options,
                                DialogInterface.OnClickListener { dialog, which ->
                                if(which == 0){ editContent(userID) }
                                if(which ==1){ deleteContent(userID) }
                            })
                            builder.show()
                        }

                        reference.articleReference.child(userID!!).addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.hasChild("type")){
                                    if(snapshot.child("type").value.toString().compareTo("video")==0){
                                        val title = snapshot.child("title").value.toString()
                                        val url = snapshot.child("url").value.toString()

                                        var vid = ""
                                        val pattern = Pattern.compile("^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$", Pattern.CASE_INSENSITIVE)
                                        val matcher = pattern.matcher(url)
                                        if (matcher.matches()){ vid = matcher.group(1) }

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
                                                    yplayer?.loadVideo(vid)
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
                                    } else if(snapshot.child("type").value.toString().compareTo("image")==0){
                                        val title = snapshot.child("title").value.toString()
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
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                    private fun deleteContent(userID: String?) {
                        reference.articleReference.child(userID!!).removeValue().addOnCompleteListener {
                            if(it.isSuccessful){
                                reference.managerReference.child(reference.currentUserId!!).child("articles").child(userID!!).removeValue().addOnCompleteListener {
                                    if(it.isSuccessful) Toast.makeText(activity,"Article Deleted",
                                        Toast.LENGTH_LONG).show()
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
            recyclerView.adapter = firebaseRecyclerAdapter
            firebaseRecyclerAdapter.startListening()
        } *//*else {
            val options = FirebaseRecyclerOptions.Builder<ContentDataClass>()
                .setQuery(reference.articleReference, ContentDataClass::class.java).build()

            val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<ContentDataClass, ContentViewHolder> =
                object : FirebaseRecyclerAdapter<ContentDataClass, ContentViewHolder>(options){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
                        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_content_manger_show_content,parent,false)
                        return ContentViewHolder(view)
                    }
                    override fun onBindViewHolder(holder: ContentViewHolder,
                                                  position: Int, model: ContentDataClass) {

                        val userID = getRef(position).key

                        reference.userReference.child(userID!!).addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.child("type").value.toString().compareTo("contentManager")!=0){
                                    holder.edit.visibility = View.GONE
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })

                        reference.articleReference.child(userID!!).addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.hasChild("type")){
                                    if(snapshot.child("type").value.toString().compareTo("video")==0){
                                        val title = snapshot.child("title").value.toString()
                                        val url = snapshot.child("url").value.toString()

                                        var vid = ""
                                        val pattern = Pattern.compile("^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$", Pattern.CASE_INSENSITIVE)
                                        val matcher = pattern.matcher(url)
                                        if (matcher.matches()){
                                            vid = matcher.group(1)
                                        }

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
                                                    yplayer?.loadVideo(vid)
                                                    yplayer?.play()
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
                                    } else if(snapshot.child("type").value.toString().compareTo("image")==0){
                                        val title = snapshot.child("title").value.toString()
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
                                }else {}
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
            recyclerView.adapter = firebaseRecyclerAdapter
            firebaseRecyclerAdapter.startListening()
        }*//*
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.contentShowTitle)
        val desc = itemView.findViewById<TextView>(R.id.contentShowDesc)
        val image = itemView.findViewById<ImageView>(R.id.contentShowImg)
        val cardView = itemView.findViewById<CardView>(R.id.contentShowCV)
        val edit = itemView.findViewById<ImageButton>(R.id.contentEdit)
    }*/
}