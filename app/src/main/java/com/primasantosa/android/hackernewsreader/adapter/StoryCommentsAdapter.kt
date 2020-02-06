package com.primasantosa.android.hackernewsreader.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.primasantosa.android.hackernewsreader.R
import com.primasantosa.android.hackernewsreader.model.StoryComments
import java.text.SimpleDateFormat
import java.util.*

class StoryCommentsAdapter : ListAdapter<StoryComments, StoryCommentsAdapter.ViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<StoryComments>() {
        override fun areItemsTheSame(oldItem: StoryComments, newItem: StoryComments): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: StoryComments, newItem: StoryComments): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Call findViewById to all mandatory views
        val author = itemView.findViewById<TextView>(R.id.story_detail_author)
        val comment = itemView.findViewById<TextView>(R.id.story_detail_comment)
        val date = itemView.findViewById<TextView>(R.id.story_detail_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_story_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get item position
        val comment = getItem(position)

        // Date formatter
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Set author TV
        holder.author.text = comment.author

        // Set comment TV
        holder.comment.text = comment.comment

        // Set date TV
        holder.date.text = formatter.format(comment.time?.times(1000))
    }
}