package com.example.booklistapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert
    suspend fun insert(bookmark: Bookmark): Long

    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId")
    fun getBookmarksForBook(bookId: Int): Flow<List<Bookmark>>

    @Query("DELETE FROM bookmarks WHERE id = :bookmarkId")
    suspend fun deleteBookmark(bookmarkId: Int)
}