package com.androiddevs.mvvmnewsapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
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
import com.androiddevs.mvvmnewsapp.ui.MainActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.NewsViewModelProviderFactory
import com.androiddevs.mvvmnewsapp.util.Constants
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Fragment for searching news articles.
 * It provides a search input field, displays search results in a RecyclerView,
 * implements search debouncing, and supports pagination.
 */
class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    // ViewModel shared with the hosting Activity.
    // This ensures data survives configuration changes and can be shared between fragments.
    private val viewModel by activityViewModels<NewsViewModel>{
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        NewsViewModelProviderFactory(requireActivity().application, newsRepository)
    }
    // Adapter for the RecyclerView
    lateinit var newsAdapter: NewsAdapter

    val TAG = "SearchNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Adjust the top padding of the search layout to account for the status bar height.
        layout_search.updatePadding(top = (requireActivity() as MainActivity).getStatusBarHeight())

        // Set a click listener for items in the RecyclerView
        newsAdapter.setOnItemClickListener { article ->
            // Create a bundle to pass the selected article to the ArticleFragment
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            // Navigate to the ArticleFragment, passing the bundle
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        // Implement search debouncing using coroutines.
        // This prevents making an API call for every character typed.
        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel() // Cancel any previous search job
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY) // Wait for a short delay after typing stops
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString()) // Trigger search
                    }
                }
            }
        }

        // Observe the searchNews LiveData from the ViewModel
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                // On success, hide progress bar and submit the list to the adapter
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        // Calculate total pages and check if it's the last page for pagination
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if (isLastPage) {
                            rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                // On error, hide progress bar and show a toast message
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred: $it", Toast.LENGTH_LONG)
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
    val scrollListener = object : RecyclerView.OnScrollListener() {
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
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            // Determine if we should paginate based on the scroll position and state.
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                // Fetch the next page of search results from the ViewModel.
                viewModel.searchNews(etSearch.text.toString())
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
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            // Attach the custom scroll listener to the RecyclerView.
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }
}

