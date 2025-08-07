package com.androiddevs.mvvmnewsapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.NewsViewModelProviderFactory
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar

/**
 * Fragment to display breaking news articles.
 * It uses a RecyclerView to show a list of articles and implements pagination
 * to load more articles as the user scrolls.
 */
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    // ViewModel shared with the hosting Activity.
    // This ensures data survives configuration changes and can be shared between fragments.
    private val viewModel by activityViewModels<NewsViewModel> {
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        NewsViewModelProviderFactory(requireActivity().application, newsRepository)
    }
    // Adapter for the RecyclerView
    lateinit var newsAdapter: NewsAdapter

    val TAG = "BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Set a click listener for items in the RecyclerView
        newsAdapter.setOnItemClickListener { article ->
            // Create a bundle to pass the selected article to the ArticleFragment
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            // Navigate to the ArticleFragment, passing the bundle
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        // Observe the breakingNews LiveData from the ViewModel
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                // On success, hide progress bar and submit the list to the adapter
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        // Calculate total pages and check if it's the last page
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage) {
                            rvBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                // On error, hide progress bar and show a toast message
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occurred: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                // On loading, show the progress bar
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    // --- Pagination Logic ---
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    // Scroll listener for the RecyclerView to implement pagination
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        // Called when the scroll state changes (e.g., start scrolling, stop scrolling).
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // Check if the user is currently scrolling by touching the screen.
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        // Called while the RecyclerView is being scrolled.
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            // Determine if we should paginate based on the scroll position and state.
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                // Fetch the next page of news from the ViewModel.
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }

    /**
     * Initializes the RecyclerView, sets its adapter and layout manager,
     * and attaches the scroll listener for pagination.
     */
    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            // Attach the custom scroll listener to the RecyclerView.
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}