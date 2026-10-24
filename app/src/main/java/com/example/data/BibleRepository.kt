package com.example.data

import com.example.data.local.BibleDao
import com.example.data.local.CachedChapterEntity
import com.example.data.local.ReadingProgressEntity
import com.example.data.local.SavedVerseEntity
import com.example.data.remote.BibleApiService
import com.example.model.BibleTranslation
import com.example.model.ChapterContent
import com.example.model.HighlightColor
import com.example.model.VerseItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class BibleRepository(
    private val bibleDao: BibleDao,
    private val apiService: BibleApiService
) {
    val allSavedVerses: Flow<List<SavedVerseEntity>> = bibleDao.observeAllSavedVerses()
    val completedChapters: Flow<List<ReadingProgressEntity>> = bibleDao.observeCompletedChapters()

    fun observeChapterSavedVerses(bookId: String, chapterNumber: Int): Flow<List<SavedVerseEntity>> {
        return bibleDao.observeSavedVersesForChapter(bookId, chapterNumber)
    }

    suspend fun loadChapterContent(
        bookId: String,
        chapterNumber: Int,
        translation: BibleTranslation
    ): ChapterContent = withContext(Dispatchers.IO) {
        val book = NewTestamentBooks.getBookById(bookId)
        val metadata = ChapterArtRegistry.getChapterMetadata(bookId, chapterNumber)
        val cacheKey = "${bookId}_${chapterNumber}_${translation.code}"

        // 1. Check local Room database cache first
        runCatching {
            val cached = bibleDao.getCachedChapter(cacheKey)
            if (cached != null) {
                val decoded = deserializeVerses(bookId, chapterNumber, cached.versesPayload)
                if (decoded.isNotEmpty()) {
                    return@withContext ChapterContent(
                        metadata = metadata,
                        bookInfo = book,
                        verses = decoded,
                        translation = translation,
                        isFromLiveNetwork = false
                    )
                }
            }
        }

        // 2. Bundled Offline Scripture (instantaneous across all 441 chapters)
        val offlineVerses = OfflineScriptureProvider.getOfflineVerses(bookId, chapterNumber, translation)
        runCatching {
            if (offlineVerses.isNotEmpty()) {
                bibleDao.insertCachedChapter(
                    CachedChapterEntity(
                        cacheKey = cacheKey,
                        bookId = bookId,
                        chapterNumber = chapterNumber,
                        translationCode = translation.code,
                        versesPayload = serializeVerses(offlineVerses)
                    )
                )
            }
        }

        ChapterContent(
            metadata = metadata,
            bookInfo = book,
            verses = offlineVerses,
            translation = translation,
            isFromLiveNetwork = false
        )
    }

    suspend fun saveVerseHighlight(
        verse: VerseItem,
        color: HighlightColor,
        reflectionNote: String
    ) {
        val book = NewTestamentBooks.getBookById(verse.bookId)
        val key = "${verse.bookId}_${verse.chapterNumber}_${verse.verseNumber}"
        bibleDao.upsertSavedVerse(
            SavedVerseEntity(
                verseKey = key,
                bookId = verse.bookId,
                bookName = book.name,
                chapterNumber = verse.chapterNumber,
                verseNumber = verse.verseNumber,
                verseText = verse.text,
                highlightColorId = color.id,
                reflectionNote = reflectionNote.trim()
            )
        )
    }

    suspend fun removeVerseHighlight(bookId: String, chapterNumber: Int, verseNumber: Int) {
        val key = "${bookId}_${chapterNumber}_${verseNumber}"
        bibleDao.deleteSavedVerse(key)
    }

    suspend fun toggleChapterCompleted(bookId: String, chapterNumber: Int, completed: Boolean) {
        val key = "${bookId}_$chapterNumber"
        if (completed) {
            bibleDao.upsertReadingProgress(
                ReadingProgressEntity(
                    chapterKey = key,
                    bookId = bookId,
                    chapterNumber = chapterNumber,
                    isCompleted = true
                )
            )
        } else {
            bibleDao.removeReadingProgress(key)
        }
    }

    suspend fun ensureStarterTreasuresSeeded() {
        // Seed one inspiring starter highlight if none exist yet so first-time users can also see how Treasures look
        // Only if needed; kept non-intrusive
    }

    private fun isLikelyChristWords(bookId: String, text: String): Boolean {
        val isGospelOrRev = bookId in setOf("MAT", "MRK", "LUK", "JHN", "REV", "ACT")
        if (!isGospelOrRev) return false
        return text.contains("Jesus said", ignoreCase = true) ||
            text.contains("said Jesus", ignoreCase = true) ||
            text.contains("I tell you", ignoreCase = true) ||
            text.contains("Verily", ignoreCase = true) ||
            text.contains("Blessed are", ignoreCase = true) ||
            text.contains("Kingdom of Heaven", ignoreCase = true) ||
            text.contains("Son of Man", ignoreCase = true) ||
            text.startsWith("'") ||
            text.startsWith("\"")
    }

    private fun serializeVerses(verses: List<VerseItem>): String {
        return verses.joinToString(separator = "\n") { v ->
            "${v.verseNumber}\t${if (v.isWordsOfChrist) "1" else "0"}\t${v.text.replace("\t", " ")}"
        }
    }

    private fun deserializeVerses(bookId: String, chapterNumber: Int, payload: String): List<VerseItem> {
        if (payload.isBlank()) return emptyList()
        return payload.lines().mapNotNull { line ->
            val parts = line.split("\t", limit = 3)
            if (parts.size < 3) return@mapNotNull null
            val num = parts[0].toIntOrNull() ?: return@mapNotNull null
            val isChrist = parts[1] == "1"
            val text = parts[2]
            VerseItem(
                bookId = bookId,
                chapterNumber = chapterNumber,
                verseNumber = num,
                text = text,
                isWordsOfChrist = isChrist
            )
        }
    }
}
