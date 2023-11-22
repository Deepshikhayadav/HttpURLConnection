package com.deepshikhayadav.moengage_test

import java.util.Date
// data class
data class NewsResponse(
    val status: String,
    val articles: List<NewsArticles>,
)

data class NewsArticles(
    val source: Source,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: Date,
    val content: String
)

data class Source(
    val id: String,
    val name: String
)
