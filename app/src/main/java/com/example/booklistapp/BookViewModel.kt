package com.example.booklistapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel to manage book data and operations.
 * @param bookDao The DAO for database interactions.
 */
class BookViewModel(private val bookDao: BookDao) : ViewModel() {
    // Expose the Flow of all books from the DAO
    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()

    /**
     * Adds a new book with the given title.
     * @param title The title of the book to add.
     */
    fun addBook(title: String) {
        viewModelScope.launch {
            bookDao.insert(Book(title = title))
        }
    }

    /**
     * Deletes the specified book.
     * @param book The book to delete.
     */
    fun deleteBook(bookId: Int) {
        viewModelScope.launch {
            bookDao.deleteBook(bookId)
        }
    }
}