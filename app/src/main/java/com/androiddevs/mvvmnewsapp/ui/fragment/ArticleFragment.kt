package com.androiddevs.mvvmnewsapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.ui.MainActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.NewsViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.fragment_search_news.*

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private val viewModel by activityViewModels<NewsViewModel>{
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        NewsViewModelProviderFactory(requireActivity().application, newsRepository)
    }
    val args: ArticleFragmentArgs by navArgs() //ArticleFragmentArgs自動生成

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (webView.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = (requireActivity() as MainActivity).getStatusBarHeight()
        }

        val article = args.article

        webView.apply {
            webViewClient = WebViewClient() //make sure that the page will always load inside of
            // this web view and don't load in the standard browser of the phone
            article.url?.let {
                loadUrl(article.url)
            }
        }

        //點擊愛心
        fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

}