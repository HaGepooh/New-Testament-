package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleDao {

    @Query("SELECT * FROM cached_chapters WHERE cacheKey = :cacheKey LIMIT 1")
    suspend fun getCachedChapter(cacheKey: String): CachedChapterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedChapter(entity: CachedChapterEntity)

    @Query("SELECT * FROM saved_verses ORDER BY savedAtMillis DESC")
    fun observeAllSavedVerses(): Flow<List<SavedVerseEntity>>

    @Query("SELECT * FROM saved_verses WHERE bookId = :bookId AND chapterNumber = :chapterNumber")
    fun observeSavedVersesForChapter(bookId: String, chapterNumber: Int): Flow<List<SavedVerseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSavedVerse(entity: SavedVerseEntity)

    @Query("DELETE FROM saved_verses WHERE verseKey = :verseKey")
    suspend fun deleteSavedVerse(verseKey: String)

    @Query("SELECT * FROM reading_progress WHERE isCompleted = 1")
    fun observeCompletedChapters(): Flow<List<ReadingProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReadingProgress(entity: ReadingProgressEntity)

    @Query("DELETE FROM reading_progress WHERE chapterKey = :chapterKey")
    suspend fun removeReadingProgress(chapterKey: String)
}
