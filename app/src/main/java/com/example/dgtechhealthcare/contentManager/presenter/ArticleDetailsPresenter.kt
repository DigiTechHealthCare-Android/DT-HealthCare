package com.example.dgtechhealthcare.contentManager.presenter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.dgtechhealthcare.contentManager.model.ArticleDetailsModel

class ArticleDetailsPresenter(view : View) {

    val model = ArticleDetailsModel(view)

    fun editArticlesPresenter(view: View, activity: FragmentActivity, articleID: String,
        cEdit: ImageView){
        model.editArticle(view,activity,articleID,cEdit)
    }

    fun populateView(articleID: String, cTitle: TextView, userName: TextView, articleViews: TextView
                     , cDesc: TextView, type: String, cImg: ImageView, userImage: ImageView
                     , requireActivity: FragmentActivity) {
        model.populateArtcile(articleID,cTitle,userName,articleViews,cDesc,type,cImg,
            userImage,requireActivity)
    }

}