package com.example.booklistapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Book::class, Bookmark::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookmarkDao(): BookmarkDao
}