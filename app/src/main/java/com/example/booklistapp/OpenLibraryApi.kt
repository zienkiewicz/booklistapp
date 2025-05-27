package com.example.booklistapp

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url


data class SearchResponse(val docs: List<BookDoc>)
data class BookDoc(
    val title: String?,
    val author_name: List<String>?,
    val cover_i: Int?,
    val key: String?
)

data class WorkDetail(
    val description: Any?
)

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBook(@Query("title") title: String): SearchResponse

    @GET
    suspend fun getWorkDetail(@Url url: String): WorkDetail
}