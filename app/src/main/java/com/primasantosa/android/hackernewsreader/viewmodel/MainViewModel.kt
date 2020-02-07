package com.primasantosa.android.hackernewsreader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.primasantosa.android.hackernewsreader.model.News
import com.primasantosa.android.hackernewsreader.model.StoryComments
import com.primasantosa.android.hackernewsreader.repository.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Initialize the repository
    private val newsRepository = NewsRepository()

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
        // Call getTopStories
        coroutineScope.launch {
            _listStories.value = newsRepository.getTopStories(_progressBar, _listStories)
        }
    }

    // Call getStory from repository
    fun getStory() {
        coroutineScope.launch {
            newsRepository.getStory(_progressBar, _story, storyId, _listStoryComments)
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