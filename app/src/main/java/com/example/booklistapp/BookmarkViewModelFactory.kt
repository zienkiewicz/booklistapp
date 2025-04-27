package com.example.booklistapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BookmarkViewModelFactory(private val bookmarkDao: BookmarkDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookmarkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookmarkViewModel(bookmarkDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}