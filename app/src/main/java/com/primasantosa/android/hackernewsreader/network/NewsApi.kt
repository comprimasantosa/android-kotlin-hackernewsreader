package com.primasantosa.android.hackernewsreader.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.primasantosa.android.hackernewsreader.model.News
import com.primasantosa.android.hackernewsreader.model.StoryComments
import com.primasantosa.android.hackernewsreader.util.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Build moshi
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Build retrofit
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface NewsApiService {
    // Get a list of top stories
    @GET("v0/topstories.json")
    fun getTopStories(): Deferred<List<Long>>

    // Get a story
    @GET("v0/item/{storyId}.json")
    fun getStory(@Path("storyId") storyId: Long): Deferred<News>

    // Get a story comment
    @GET("v0/item/{commentId}.json")
    fun getComment(@Path("commentId") commentId: Long): Deferred<StoryComments>
}

object NewsApi {
    // Create retrofit service
    val retrofitService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}