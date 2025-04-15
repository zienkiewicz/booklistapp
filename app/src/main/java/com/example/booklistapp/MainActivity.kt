package com.example.booklistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Add this import
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.booklistapp.ui.theme.BooklistappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BooklistappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val bookDao = DatabaseProvider.getDatabase(context).bookDao()
                    val viewModel: BookViewModel = viewModel(factory = BookViewModelFactory(bookDao))
                    BookListScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BookListScreen(viewModel: BookViewModel) {
    val books by viewModel.allBooks.collectAsStateWithLifecycle(initialValue = emptyList())
    var newTitle by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = newTitle,
            onValueChange = { newTitle = it },
            label = { Text("Book Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (newTitle.isNotBlank()) {
                    viewModel.addBook(newTitle)
                    newTitle = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add Book")
        }
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(books) { book ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(book.title)
                    Button(onClick = { viewModel.deleteBook(book.id) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}