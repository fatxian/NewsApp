package com.androiddevs.mvvmnewsapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.NewsViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

/**
 * Fragment to display saved news articles from the local database.
 * It allows users to view saved articles, navigate to their details,
 * and delete them using a swipe-to-delete gesture.
 */
class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    // ViewModel shared with the hosting Activity.
    // This ensures data survives configuration changes and can be shared between fragments.
    private val viewModel by activityViewModels<NewsViewModel>{
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        NewsViewModelProviderFactory(requireActivity().application, newsRepository)
    }
    // Adapter for the RecyclerView
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Set a click listener for items in the RecyclerView
        newsAdapter.setOnItemClickListener {
            // Create a bundle to pass the selected article to the ArticleFragment
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            // Navigate to the ArticleFragment, passing the bundle
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        // Configure ItemTouchHelper for swipe-to-delete functionality
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, // Drag directions (not used here)
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Swipe directions
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true // Not implementing move functionality
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                // Delete the article from the database via the ViewModel
                viewModel.deleteArticle(article)
                // Show a Snackbar with an undo option
                Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        // If "Undo" is clicked, re-save the article
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        // Attach the ItemTouchHelper to the RecyclerView
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }

        // Observe the LiveData of saved news from the ViewModel
        // and update the RecyclerView adapter when data changes.
        viewModel.getSavedNews().observe(viewLifecycleOwner) { articles ->
            newsAdapter.differ.submitList(articles)
            // Show/hide "nothing here" text based on whether there are saved articles
            if (articles.isNotEmpty()) {
                tv_nothing_here.visibility = View.INVISIBLE
            } else {
                tv_nothing_here.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Initializes the RecyclerView, sets its adapter and layout manager.
     */
    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}