package com.primasantosa.android.hackernewsreader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.primasantosa.android.hackernewsreader.model.News
import com.primasantosa.android.hackernewsreader.model.StoryComments
import com.primasantosa.android.hackernewsreader.network.NewsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel : ViewModel() {
    // Store list of Stories data
    private val _listStories = MutableLiveData<List<News>>()
    val listStories: LiveData<List<News>>
        get() = _listStories

    // Store a top story ID to pass to StoryDetailActivity
    private val _storyId = MutableLiveData<Long>()
    val storyId: LiveData<Long>
        get() = _storyId

    // Store a story data
    private val _story = MutableLiveData<News>()
    val story: LiveData<News>
        get() = _story

    // Store a story comments
    private val _listStoryComments = MutableLiveData<List<StoryComments>>()
    val listStoryComments: LiveData<List<StoryComments>>
        get() = _listStoryComments

    // Store favorite story
    private var _favoriteStory = MutableLiveData<String?>()
    val favoriteStory: LiveData<String?>
        get() = _favoriteStory

    // Track status of progress bar
    private val _progressBar = MutableLiveData<Boolean>()
    val progressBar: LiveData<Boolean>
        get() = _progressBar

    // Declare a coroutine job and a coroutine scope
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        // Call getTopStories() when initialize the ViewModel
        getTopStories()
    }

    // Calling a function to fetch network data in ViewModel is not ideal. This is temporary until it moved to Repository.
    private fun getTopStories() {
        // Declare a mutableList that will hold list of stories
        val storyList = mutableListOf<News>()

        // Execute API Call in coroutine scope
        coroutineScope.launch {
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
//                Timber.i("$storyList")
            } catch (e: Exception) {
                Timber.i("$e")
            }
        }
    }

    // Get a story
    fun getStory() {
        // Declare a mutableList that will hold list of story comments
        val storyCommentList = mutableListOf<StoryComments>()

        coroutineScope.launch {
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

    // Set story ID to pass from OnClickListener to StoryDetailActivity
    fun setStoryId(storyId: Long) {
        this._storyId.value = storyId
    }

    // Set favorite story title to pass to MainActivity
    fun setFavoriteStoryTitle(storyTitle: String?) {
        this._favoriteStory.value = storyTitle
    }

    override fun onCleared() {
        super.onCleared()

        // Cancel coroutine job
        viewModelJob.cancel()
    }

}