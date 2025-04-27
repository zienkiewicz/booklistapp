package com.example.booklistapp

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val description: String,
    val page: Int // new page field
)