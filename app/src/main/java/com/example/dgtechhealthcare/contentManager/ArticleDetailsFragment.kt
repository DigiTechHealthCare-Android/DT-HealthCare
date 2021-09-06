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
import kotlinx.android.synthetic.main.fragment_article_details.*

class ArticleDetailsFragment : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var detailsPresenter : ArticleDetailsPresenter

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

        detailsPresenter.editArticlesPresenter(view,requireActivity(),articleID,contentEdit)
        detailsPresenter.populateView(articleID,contentShowTitle,articlePTextView,countViews
            ,contentShowDesc,type,contentShowImg,articlePImageView,requireActivity())
    }

    private fun initializeValues(view: View) {
        reference = FirebasePresenter(view)
        detailsPresenter = ArticleDetailsPresenter(view)
    }
}