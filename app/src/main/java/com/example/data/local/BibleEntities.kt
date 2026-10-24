package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_chapters")
data class CachedChapterEntity(
    @PrimaryKey val cacheKey: String, // e.g. "MAT_5_web"
    val bookId: String,
    val chapterNumber: Int,
    val translationCode: String,
    val versesPayload: String, // serialized verseNumber|text|isChrist lines
    val cachedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_verses")
data class SavedVerseEntity(
    @PrimaryKey val verseKey: String, // e.g. "JHN_3_16"
    val bookId: String,
    val bookName: String,
    val chapterNumber: Int,
    val verseNumber: Int,
    val verseText: String,
    val highlightColorId: String, // GOLD, RUBY, EMERALD, SAPPHIRE
    val reflectionNote: String = "",
    val savedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val chapterKey: String, // e.g. "MAT_1"
    val bookId: String,
    val chapterNumber: Int,
    val isCompleted: Boolean,
    val completedAtMillis: Long = System.currentTimeMillis()
)
