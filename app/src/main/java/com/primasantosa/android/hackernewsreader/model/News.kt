package com.primasantosa.android.hackernewsreader.model

import com.squareup.moshi.Json

data class News(
    @Json(name = "id") val id: Long,
    @Json(name = "by") val author: String,
    @Json(name = "time") val time: Long,
    @Json(name = "title") val title: String,
    @Json(name = "text") val description: String?,
    @Json(name = "score") val score: Int,
    @Json(name = "kids") val comments: List<Long>?
)

data class StoryComments(
    @Json(name = "by") val author: String?,
    @Json(name = "id") val id: Long?,
    @Json(name = "text") val comment: String?,
    @Json(name = "time") val time: Long?
)