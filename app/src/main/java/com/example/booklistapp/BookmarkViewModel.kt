package com.example.booklistapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BookmarkViewModel(private val bookmarkDao: BookmarkDao) : ViewModel() {
    fun getBookmarksForBook(bookId: Int): Flow<List<Bookmark>> =
        bookmarkDao.getBookmarksForBook(bookId)

    fun addBookmark(bookId: Int, description: String, page: Int) {
        viewModelScope.launch {
            bookmarkDao.insert(Bookmark(bookId = bookId, description = description, page = page))
        }
    }

    fun deleteBookmark(bookmarkId: Int) {
        viewModelScope.launch {
            bookmarkDao.deleteBookmark(bookmarkId)
        }
    }
}
