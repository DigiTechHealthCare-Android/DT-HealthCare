package com.example.dgtechhealthcare.contentManager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.example.dgtechhealthcare.utils.ViewPdfActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ContentMangerShowContentFragment(val view: View) {

    val reference = FirebasePresenter(view)

    fun displayOnlyArticles(recyclerView: RecyclerView,activity: FragmentActivity,role:String){

        if(role.compareTo("contentManager")==0){

            val options = FirebaseRecyclerOptions.Builder<ManagerDataClass>()
                .setQuery(reference.managerReference.child(reference.currentUserId!!)
                    .child("articles"),ManagerDataClass::class.java).build()

            val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<ManagerDataClass,ArticleViewHolder> =
                object : FirebaseRecyclerAdapter<ManagerDataClass,ArticleViewHolder>(options){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
                        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_content_manger_show_content,parent,false)
                        return ArticleViewHolder(view)
                    }

                    override fun onBindViewHolder(holder: ArticleViewHolder,
                        position: Int, model: ManagerDataClass) {

                        var type = ""
                        val userID = getRef(position).key
                        reference.articleReference.child(userID!!).addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                type = snapshot.child("type").value.toString()

                                val desc = snapshot.child("desc").value.toString()
                                if(desc.isNullOrEmpty()) holder.desc.setText("No Description Found")
                                else holder.desc.setText(desc)
                                val title = snapshot.child("title").value.toString()
                                holder.title.setText(title)

                                val name = snapshot.child("publisherName").value.toString()
                                holder.name.setText(name)
                                val img = snapshot.child("publisherImage").value.toString()
                                Picasso.get().load(img).into(holder.img)
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })

                        holder.card.setOnClickListener {

                            reference.articleReference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val views = snapshot.child("views").value.toString()
                                    val v : Int = views.toInt()
                                    reference.articleReference.child(userID).child("views").setValue(v + 1)
                                }
                                override fun onCancelled(error: DatabaseError) {}
                            })
                            try {
                                val frag = ArticleDetailsFragment()
                                val bundle = Bundle()
                                bundle.putString("articleID",userID)
                                bundle.putString("type", type)
                                frag.arguments = bundle
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.contentFrame,frag).addToBackStack(null).commit()
                            } catch (e : Exception){
                                Toast.makeText(activity,R.string.try_again,Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            recyclerView.adapter = firebaseRecyclerAdapter
            firebaseRecyclerAdapter.startListening()
        } else if(role.compareTo("patient")==0){

            val options = FirebaseRecyclerOptions.Builder<ContentDataClass>()
                .setQuery(reference.articleReference,ContentDataClass::class.java).build()

            val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<ContentDataClass,ArticleViewHolder> =
                object : FirebaseRecyclerAdapter<ContentDataClass,ArticleViewHolder>(options){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
                        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_content_manger_show_content,parent,false)
                        return ArticleViewHolder(view)
                    }

                    override fun onBindViewHolder(holder: ArticleViewHolder,
                        position: Int,model: ContentDataClass) {
                        var type = ""
                        var researchUrl = ""
                        var url = ""
                        var checkUrl : Boolean = false
                        val userID = getRef(position).key
                        reference.articleReference.child(userID!!).addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                type = snapshot.child("type").value.toString()
                                researchUrl = snapshot.child("researchRef").value.toString()
                                url = snapshot.child("url").value.toString()
                                if (snapshot.hasChild("researchRef")) checkUrl = true

                                val desc = snapshot.child("desc").value.toString()
                                if(desc.isNullOrEmpty()) holder.desc.setText("No Description Found")
                                else holder.desc.setText(desc)
                                val title = snapshot.child("title").value.toString()
                                holder.title.setText(title)

                                val name = snapshot.child("publisherName").value.toString()
                                holder.name.setText(name)
                                val img = snapshot.child("publisherImage").value.toString()
                                Picasso.get().load(img).into(holder.img)
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })

                        holder.card.setOnClickListener {
                            reference.articleReference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val views = snapshot.child("views").value.toString()
                                    val v : Int = views.toInt()
                                    reference.articleReference.child(userID).child("views").setValue(v + 1)
                                }
                                override fun onCancelled(error: DatabaseError) {}
                            })
                            if(type.compareTo("research")==0){
                                if (checkUrl){
                                    val i = Intent(activity, ViewPdfActivity::class.java)
                                    i.putExtra("url",researchUrl)
                                    activity.startActivity(i)
                                } else {
                                    val i = Intent(activity,ViewPdfActivity::class.java)
                                    i.putExtra("url",url)
                                    activity.startActivity(i)
                                }

                            } else {
                                val frag = ArticleDetailsFragment()
                                val bundle = Bundle()
                                bundle.putString("articleID",userID)
                                bundle.putString("type", type)
                                frag.arguments = bundle
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.swipeLayout,frag).addToBackStack(null).commit()
                            }
                        }
                    }

                }
            recyclerView.adapter = firebaseRecyclerAdapter
            firebaseRecyclerAdapter.startListening()
        }
    }

    inner class ArticleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.articleTitle)
        val desc = itemView.findViewById<TextView>(R.id.articleDesc)
        val name = itemView.findViewById<TextView>(R.id.articlePostedByName)
        val img = itemView.findViewById<ImageView>(R.id.articlePostIView)
        val card = itemView.findViewById<CardView>(R.id.contentShowCV)
    }
}