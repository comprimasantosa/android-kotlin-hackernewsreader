package com.primasantosa.android.hackernewsreader.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.primasantosa.android.hackernewsreader.R
import com.primasantosa.android.hackernewsreader.adapter.StoryCommentsAdapter
import com.primasantosa.android.hackernewsreader.databinding.ActivityStoryDetailBinding
import com.primasantosa.android.hackernewsreader.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class StoryDetailActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set content view
        val binding: ActivityStoryDetailBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_story_detail)

        // Observe progress bar status
        viewModel.progressBar.observe(this, Observer {
            if (it) {
                binding.storyDetailProgressBar.visibility = View.VISIBLE
            } else {
                binding.storyDetailProgressBar.visibility = View.GONE
            }
        })

        // Date formatter
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Set story ID to pass to ViewModel so it can call the API
        val storyId = intent.getLongExtra("storyId", 0)
        viewModel.setStoryId(storyId)

        // Trigger the API
        viewModel.getStory()

        // Observe a story detail
        viewModel.story.observe(this, Observer {
            binding.storyTitle.text = it.title
            binding.storyAuthor.text = "by ${it.author}"

            if (it.description != null) {
                binding.storyDescription.text = it.description
            } else {
                binding.storyDescription.text = getString(R.string.no_description_text)
            }
            binding.storyDate.text = formatter.format(it.time * 1000)
        })

        // Set RV adapter
        val storyCommentsAdapter = StoryCommentsAdapter()
        binding.storyDetailRv.adapter = storyCommentsAdapter

        // Pass a list of story comments to RV
        viewModel.listStoryComments.observe(this, Observer {
            if (it == null || it.isEmpty()) {
                binding.noCommentsText.visibility = View.VISIBLE //TODO()
            } else {
                storyCommentsAdapter.submitList(it)
                binding.noCommentsText.visibility = View.GONE
            }
        })

        // Make a story favorite
        binding.storyDetailStarButton.setOnClickListener {
            if (viewModel.favoriteStory.value != viewModel.story.value?.title) {
                viewModel.setFavoriteStoryTitle(viewModel.story.value?.title)
                binding.storyDetailStarButton.setImageDrawable(getDrawable(R.drawable.ic_star_colored))
            } else {
                viewModel.setFavoriteStoryTitle(null)
                binding.storyDetailStarButton.setImageDrawable(getDrawable(R.drawable.ic_star))
            }
        }
    }

    override fun onBackPressed() {
        // Pass story title back to MainActivity
        val intent = Intent().apply {
            putExtra("FavoriteStoryTitle", viewModel.favoriteStory.value)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }
}
