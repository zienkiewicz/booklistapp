package com.example.booklistapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.booklistapp.ApiClient

/**
 * ViewModel to manage book data and operations.
 * @param bookDao The DAO for database interactions.
 */
class BookViewModel(private val bookDao: BookDao) : ViewModel() {
    // Expose the Flow of all books from the DAO
    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()

    fun addBook(title: String) {
        viewModelScope.launch {
            val book = fetchBookDetails(title)
            bookDao.insert(book)
        }
    }

    private suspend fun fetchBookDetails(title: String): Book {
        return withContext(Dispatchers.IO) {
            try {
                val searchResponse = ApiClient.api.searchBook(title)
                val doc: BookDoc? = searchResponse.docs.firstOrNull()

                val bookTitle = doc?.title ?: title
                val author = doc?.author_name?.firstOrNull() ?: "Unknown author"
                val coverUrl = doc?.cover_i
                    ?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }

                val rawDescription: Any? = doc?.key
                    ?.let { ApiClient.api.getWorkDetail("https://openlibrary.org${it}.json").description }

                val description = when (rawDescription) {
                    is String -> rawDescription
                    is Map<*, *> -> rawDescription["value"] as? String
                    else -> null
                } ?: "No description available"

                Book(
                    title = bookTitle,
                    author = author,
                    description = description,
                    coverUrl = coverUrl
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Book(title = title)
            }
        }
    }
    /*
    private suspend fun fetchBookDetails(title: String): Book {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.api.searchBook(title)
                val doc = response.docs.firstOrNull()

                // Extract description, author, cover
                val description = doc?.first_sentence?.firstOrNull()?.removeSurrounding("\"", "\"")
                    ?: "No description available"
                val author = doc?.author_name?.firstOrNull() ?: "Unknown author"
                val coverUrl = doc?.cover_i?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }

                Book(
                    title = doc?.title ?: title,
                    author = author,
                    description = description,
                    coverUrl = coverUrl
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Book(title = title)
            }
        }
    }
    */

    fun deleteBook(bookId: Int) {
        viewModelScope.launch {
            bookDao.deleteBook(bookId)
        }
    }
}