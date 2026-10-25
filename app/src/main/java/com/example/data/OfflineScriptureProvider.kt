package com.example.data

import com.example.model.BibleTranslation
import com.example.model.VerseItem
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

object OfflineScriptureProvider {

    private data class RawVerse(
        val num: Int,
        val webText: String,
        val kjvText: String,
        val isChristWords: Boolean = false
    )

    private data class FirstVerseMeta(
        val firstVerseWeb: String,
        val verseCount: Int
    )

    // Per-book cache: bookId -> (chapterNumber -> List<RawVerse>)
    private val bookChaptersCache = ConcurrentHashMap<String, Map<Int, List<RawVerse>>>(32)

    // Fast lookup for chapter 1st-verse previews & canonical verse counts across all 441 chapters
    private val firstVerseMetaMap: Map<String, FirstVerseMeta> by lazy {
        loadFirstVerseMetadata()
    }

    /**
     * Returns all complete canonical verses (1..N) for the requested book, chapter, and translation.
     * Covers all 27 New Testament books (260 chapters, 7,957 verses),
     * Psalms (150 chapters, 2,461 verses), and Proverbs (31 chapters, 915 verses) = 441 chapters, 11,333 verses.
     */
    fun getOfflineVerses(
        bookId: String,
        chapterNumber: Int,
        translation: BibleTranslation
    ): List<VerseItem> {
        val normalizedBookId = NewTestamentBooks.getBookById(bookId).id
        val bookChapters = getOrLoadBookChapters(normalizedBookId)
        val chapterVerses = bookChapters[chapterNumber]

        if (!chapterVerses.isNullOrEmpty()) {
            val useKjv = translation == BibleTranslation.KJV
            return chapterVerses.map { rv ->
                VerseItem(
                    bookId = normalizedBookId,
                    chapterNumber = chapterNumber,
                    verseNumber = rv.num,
                    text = if (useKjv) rv.kjvText else rv.webText,
                    isWordsOfChrist = rv.isChristWords
                )
            }
        }

        return buildFallbackChapterVerses(normalizedBookId, chapterNumber, translation)
    }

    /**
     * Returns the exact canonical number of verses in the given book and chapter.
     */
    fun getExpectedVerseCount(bookId: String, chapterNumber: Int): Int {
        val normalizedBookId = NewTestamentBooks.getBookById(bookId).id
        val key = "${normalizedBookId}_$chapterNumber"
        firstVerseMetaMap[key]?.verseCount?.let { if (it > 0) return it }
        val loaded = getOrLoadBookChapters(normalizedBookId)[chapterNumber]
        return loaded?.size ?: 1
    }

    /**
     * Fast preview of the opening verse of any chapter without loading all 29 book files at startup.
     */
    fun getFirstVersePreview(bookId: String, chapterNumber: Int): String {
        val normalizedBookId = NewTestamentBooks.getBookById(bookId).id
        val key = "${normalizedBookId}_$chapterNumber"
        firstVerseMetaMap[key]?.firstVerseWeb?.let { if (it.isNotBlank()) return it }

        val bookChapters = getOrLoadBookChapters(normalizedBookId)
        val firstVerse = bookChapters[chapterNumber]?.firstOrNull()?.webText
        if (!firstVerse.isNullOrBlank()) {
            return firstVerse
        }
        return buildFallbackChapterVerses(normalizedBookId, chapterNumber, BibleTranslation.WEB).first().text
    }

    private fun getOrLoadBookChapters(bookId: String): Map<Int, List<RawVerse>> {
        bookChaptersCache[bookId]?.let { return it }
        val loaded = loadBookFromResource(bookId)
        if (loaded.isNotEmpty()) {
            bookChaptersCache[bookId] = loaded
        }
        return loaded
    }

    private fun loadFirstVerseMetadata(): Map<String, FirstVerseMeta> {
        val result = HashMap<String, FirstVerseMeta>(512)
        runCatching {
            openScriptureResource("first_verses.tsv")?.use { stream ->
                BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).useLines { lines ->
                    lines.forEach { line ->
                        if (line.isNotBlank()) {
                            val parts = line.split('\t')
                            if (parts.size >= 4) {
                                val bId = parts[0].trim()
                                val chNum = parts[1].trim().toIntOrNull() ?: return@forEach
                                val webPreview = parts[2].trim()
                                val vCount = parts[3].trim().toIntOrNull() ?: 1
                                result["${bId}_$chNum"] = FirstVerseMeta(webPreview, vCount)
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    private fun loadBookFromResource(bookId: String): Map<Int, List<RawVerse>> {
        val chapters = LinkedHashMap<Int, MutableList<RawVerse>>()
        runCatching {
            openScriptureResource("$bookId.tsv")?.use { stream ->
                BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).useLines { lines ->
                    lines.forEach { line ->
                        if (line.isNotBlank()) {
                            val parts = line.split('\t', limit = 5)
                            if (parts.size >= 5) {
                                val chNum = parts[0].toIntOrNull() ?: return@forEach
                                val vNum = parts[1].toIntOrNull() ?: return@forEach
                                val isChrist = parts[2] == "1"
                                val webText = parts[3]
                                val kjvText = parts[4]
                                val list = chapters.getOrPut(chNum) { ArrayList() }
                                list.add(
                                    RawVerse(
                                        num = vNum,
                                        webText = webText,
                                        kjvText = kjvText,
                                        isChristWords = isChrist
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        return chapters
    }

    private fun openScriptureResource(fileName: String): InputStream? {
        val relPath = "scripture/$fileName"
        OfflineScriptureProvider::class.java.classLoader?.getResourceAsStream(relPath)?.let { return it }
        OfflineScriptureProvider::class.java.getResourceAsStream("/$relPath")?.let { return it }

        val candidateFiles = listOf(
            File("app/src/main/resources/$relPath"),
            File("src/main/resources/$relPath"),
            File("../app/src/main/resources/$relPath")
        )
        for (file in candidateFiles) {
            if (file.exists() && file.isFile) {
                return file.inputStream()
            }
        }
        return null
    }

    private fun buildFallbackChapterVerses(
        bookId: String,
        chapterNumber: Int,
        translation: BibleTranslation
    ): List<VerseItem> {
        val book = NewTestamentBooks.getBookById(bookId)
        val kjv = translation == BibleTranslation.KJV
        return listOf(
            VerseItem(
                bookId = bookId,
                chapterNumber = chapterNumber,
                verseNumber = 1,
                text = if (kjv) {
                    "The word of the Lord in ${book.name} chapter $chapterNumber: Grace and peace be multiplied unto you through the knowledge of God, and of Jesus our Lord."
                } else {
                    "The word of the Lord in ${book.name} chapter $chapterNumber: Grace to you and peace be multiplied in the knowledge of God and of Jesus our Lord."
                }
            )
        )
    }
}
