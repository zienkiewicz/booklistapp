package com.example.booklistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val db = DatabaseProvider.getDatabase(context)
                    val bookViewModel: BookViewModel = viewModel(
                        factory = BookViewModelFactory(db.bookDao())
                    )
                    val bookmarkViewModel: BookmarkViewModel = viewModel(
                        factory = BookmarkViewModelFactory(db.bookmarkDao())
                    )
                    BookListScreen(bookViewModel, bookmarkViewModel)
                }
            }
        }
    }
}

@Composable
fun BookListScreen(
    bookViewModel: BookViewModel,
    bookmarkViewModel: BookmarkViewModel
) {
    val books by bookViewModel.allBooks.collectAsStateWithLifecycle(emptyList())
    var newTitle by remember { mutableStateOf("") }
    var selectedBookId by remember { mutableStateOf<Int?>(null) }
    var newBookmarkDescription by remember { mutableStateOf("") }
    var newBookmarkPage by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("New book title") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    if (newTitle.isNotBlank()) {
                        bookViewModel.addBook(newTitle)
                        newTitle = ""
                    }
                }) {
                    Text("Add")
                }
            }
        }

        items(books) { book ->
            var expanded by remember(book.id) { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (book.coverUrl != null) {
                        AsyncImage(
                            model = book.coverUrl,
                            contentDescription = "Book Cover",
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.LightGray)
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(book.title, style = MaterialTheme.typography.titleMedium)
                        Text(book.author ?: "Unknown author", style = MaterialTheme.typography.bodySmall)
                        Text(book.description ?: "No description", style = MaterialTheme.typography.bodySmall)
                    }

                    IconButton(onClick = { bookViewModel.deleteBook(book.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Book")
                    }

                    Spacer(Modifier.width(4.dp))

                    Button(onClick = {
                        expanded = !expanded
                        selectedBookId = if (expanded) book.id else null
                    }) {
                        Text(if (expanded) "Hide" else "Show")
                    }
                }

                if (expanded && selectedBookId == book.id) {
                    val bookmarks by bookmarkViewModel
                        .getBookmarksForBook(book.id)
                        .collectAsStateWithLifecycle(emptyList())

                    Column(modifier = Modifier.padding(start = 56.dp, top = 8.dp)) {
                        TextField(
                            value = newBookmarkDescription,
                            onValueChange = { newBookmarkDescription = it },
                            label = { Text("Bookmark desc.") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(4.dp))
                        TextField(
                            value = newBookmarkPage,
                            onValueChange = { newBookmarkPage = it.filter(Char::isDigit) },
                            label = { Text("Page #") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(4.dp))
                        Button(onClick = {
                            val page = newBookmarkPage.toIntOrNull() ?: 0
                            if (newBookmarkDescription.isNotBlank()) {
                                bookmarkViewModel.addBookmark(
                                    book.id,
                                    newBookmarkDescription,
                                    page
                                )
                                newBookmarkDescription = ""
                                newBookmarkPage = ""
                            }
                        }) {
                            Text("Add BM")
                        }

                        Spacer(Modifier.height(8.dp))
                        if (bookmarks.isEmpty()) {
                            Text("No bookmarks", style = MaterialTheme.typography.bodySmall)
                        } else {
                            bookmarks.forEach { bm ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        "[Pg ${bm.page}] ${bm.description}",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Button(onClick = { bookmarkViewModel.deleteBookmark(bm.id) }) {
                                        Text("Del")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
