package com.example.dgtechhealthcare.contentManager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.contentManager.presenter.ArticleDetailsPresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter

class ArticleDetailsFragment : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var detailsPresenter : ArticleDetailsPresenter

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

        detailsPresenter.editArticlesPresenter(view,requireActivity(),articleID,cEdit)
        detailsPresenter.populateView(articleID,cTitle,userName,articleViews
            ,cDesc,type,cImg,userImage,requireActivity())
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
        detailsPresenter = ArticleDetailsPresenter(view)
    }
}