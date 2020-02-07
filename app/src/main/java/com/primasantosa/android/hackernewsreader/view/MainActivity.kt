package com.primasantosa.android.hackernewsreader.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.primasantosa.android.hackernewsreader.R
import com.primasantosa.android.hackernewsreader.adapter.TopStoriesAdapter
import com.primasantosa.android.hackernewsreader.databinding.ActivityMainBinding
import com.primasantosa.android.hackernewsreader.util.FAVORITE_STORY
import com.primasantosa.android.hackernewsreader.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    // Lazily initialize the ViewModel
    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    // Lateinit DataBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // SetContentView
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Observe progress bar status
        viewModel.progressBar.observe(this, Observer {
            if (it) {
                binding.topStoriesProgressBar.visibility = View.VISIBLE
            } else {
                binding.topStoriesProgressBar.visibility = View.GONE
            }
        })

        // Declare and set RV Adapter
        val topStoriesAdapter = TopStoriesAdapter(TopStoriesAdapter.ClickListener {
            val intent = Intent(this, StoryDetailActivity::class.java).apply {
                putExtra("storyId", it)
            }
            startActivityForResult(intent, FAVORITE_STORY)
        })
        binding.topStoriesRv.adapter = topStoriesAdapter

        // Pass the list to RV
        viewModel.listStories.observe(this, Observer {
            topStoriesAdapter.submitList(it)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Set favorite story title from StoryActivityDetail
        if (requestCode == 400 && resultCode == Activity.RESULT_OK) {
            val favoriteStoryTitle = data?.getStringExtra("FavoriteStoryTitle")

            if (favoriteStoryTitle != null) {
                viewModel.setFavoriteStoryTitle(favoriteStoryTitle)
                binding.favTitle.text = viewModel.favoriteStory.value
            } else {
                binding.favTitle.text = getString(R.string.no_favorite_story_text)
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        // Set favorite story title on activity resume
        if (viewModel.favoriteStory.value == null) {
            binding.favTitle.text = getString(R.string.no_favorite_story_text)
        } else {
            binding.favTitle.text = viewModel.favoriteStory.value
        }
    }
}
