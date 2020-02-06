package com.primasantosa.android.hackernewsreader.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.primasantosa.android.hackernewsreader.R
import com.primasantosa.android.hackernewsreader.model.News
import java.text.SimpleDateFormat
import java.util.*

class TopStoriesAdapter(private val clickListener: ClickListener) :
    ListAdapter<News, TopStoriesAdapter.ViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_top_stories, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get item position
        val story = getItem(position)

        // Date formatter
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Set story title
        holder.title.text = story.title

        // Set story author
        holder.author.text = " by ${story.author} |"

        // Set story points
        holder.points.text = "${story.score} points |"

        // Set story date
        holder.date.text = " ${formatter.format(story.time * 1000)} |"

        // Set story comments
        when (story.comments?.size) {
            null -> holder.comments.text = " 0 comment"
            1 -> holder.comments.text = " 1 comment"
            else -> holder.comments.text = " ${story.comments.size} comment(s)"
        }

        // Set OnClickListener to itemView
        holder.itemView.setOnClickListener { clickListener.onClick(story) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Call findViewById for all mandatory views
        val title = itemView.findViewById<TextView>(R.id.top_stories_title)
        val author = itemView.findViewById<TextView>(R.id.top_stories_author)
        val date = itemView.findViewById<TextView>(R.id.top_stories_time)
        val points = itemView.findViewById<TextView>(R.id.top_stories_points)
        val comments = itemView.findViewById<TextView>(R.id.top_stories_comments)
    }

    class ClickListener(val clickListener: (storyId: Long) -> Unit) {
        fun onClick(news: News) = clickListener(news.id)
    }
}