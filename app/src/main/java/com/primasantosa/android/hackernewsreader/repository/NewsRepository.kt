package com.primasantosa.android.hackernewsreader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.primasantosa.android.hackernewsreader.model.News
import com.primasantosa.android.hackernewsreader.model.StoryComments
import com.primasantosa.android.hackernewsreader.network.NewsApi
import timber.log.Timber

class NewsRepository {
    // Get a list of top stories
    suspend fun getTopStories(
        _progressBar: MutableLiveData<Boolean>,
        _listStories: MutableLiveData<List<News>>
    ): List<News> {
        // Declare a mutableList that will hold list of stories
        val storyList = mutableListOf<News>()

        // Call the API
        val storyIdDeferred = NewsApi.retrofitService.getTopStories()

        try {
            // Set progress bar
            _progressBar.value = true

            // Get a list of story IDs
            val storyId = storyIdDeferred.await()

            // Loop through story IDs
            for (id in storyId) {

                // Limit to 20 Top Stories for faster data fetching
                if (id == storyId[20]) break

                // Again, call the API
                val storyDeferred = NewsApi.retrofitService.getStory(id)

                // Get story detail
                val story = storyDeferred.await()

                // Add it to list
                storyList.add(story)
            }

            // Set MutableLiveData value
            _listStories.value = storyList

            // Set progress bar
            _progressBar.value = false

            // Logging purpose
//            Timber.i("$storyList")
        } catch (e: Exception) {
            Timber.i("$e")
        }

        return storyList
    }

    // Get a story
    suspend fun getStory(
        _progressBar: MutableLiveData<Boolean>,
        _story: MutableLiveData<News>,
        storyId: LiveData<Long>,
        _listStoryComments: MutableLiveData<List<StoryComments>>
    ) {
        // Declare a mutableList that will hold list of story comments
        val storyCommentList = mutableListOf<StoryComments>()

        // Call the API
        val storyDeferred = NewsApi.retrofitService.getStory(storyId.value!!)

        try {
            // Set progress bar
            _progressBar.value = true

            // Get the story data
            val story = storyDeferred.await()

            // Store the data to _story as MutableLiveData
            _story.value = story

            // Check if story comment is null
            if (story.comments != null) {

                // Loop through story comments
                for (comment in story.comments) {

                    // Limit to 10 story comments for faster data fetching
                    if (story.comments.size > 10 && comment == story.comments[10]) break

                    // Call the API
                    val commentDeferred = NewsApi.retrofitService.getComment(comment)

                    // Get a story comment
                    val storyComments = commentDeferred.await()

                    // Add it to a list of story comments
                    storyCommentList.add(storyComments)
                }

                // Store list to _listStoryComments as LiveData
                _listStoryComments.value = storyCommentList

                // Set progress bar
                _progressBar.value = false

                // Logging purpose
//                    Timber.i("$commentList")
            }
        } catch (e: Exception) {
            Timber.i("$e")

        }

    }
}