package com.androiddevs.mvvmnewsapp.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.models.Article
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_preview.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView Adapter for displaying a list of news articles.
 */
class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    /**
     * ViewHolder holds the views for a single list item.
     * Note: This class is empty because it uses Kotlin Android Extensions for view access, which is now deprecated.
     */
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // DiffUtil calculates the difference between two lists to efficiently update the RecyclerView.
    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            // Articles are considered the same if their URLs are identical.
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            // Since Article is a data class, `==` checks for content equality.
            return oldItem == newItem
        }
    }

    // AsyncListDiffer computes the diff on a background thread.
    val differ = AsyncListDiffer(this, differCallback)

    /** Creates a new ViewHolder when the RecyclerView needs one. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }

    /** Returns the total number of items in the list. */
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /** Binds the data from an Article object to the views in the ViewHolder. */
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            // Load the article image using Glide.
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            // Set the text for the views.
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description

            // Parse and format the publication date to a "time ago" format.
            val dateFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val date: Date = dateFormatter.parse(article.publishedAt!!)!!
            val dateLong = date.time
            val timeAgo = DateUtils.getRelativeTimeSpanString(dateLong) as String
            tvPublishedAt.text = timeAgo

            // Set the click listener for the item.
            setOnClickListener {
                onItemClickListener?.let { it(article) }
            }
        }
    }

    // A lambda function to handle item clicks, passed from the Fragment/Activity.
    private var onItemClickListener: ((Article) -> Unit)? = null

    /** Public function to set the click listener from outside the adapter. */
    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

}