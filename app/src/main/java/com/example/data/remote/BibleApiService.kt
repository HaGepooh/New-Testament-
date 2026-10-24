package com.example.data.remote

data class BibleApiResponse(
    val reference: String? = null,
    val verses: List<BibleApiVerseDto>? = null,
    val text: String? = null,
    val translationId: String? = null
)

data class BibleApiVerseDto(
    val bookId: String? = null,
    val bookName: String? = null,
    val chapter: Int? = null,
    val verse: Int? = null,
    val text: String? = null
)

/**
 * Local offline-first scripture service interface.
 * All 441 chapters (27 New Testament books + 150 Psalms + 31 Proverbs) in both WEB and KJV
 * are bundled inside [com.example.data.OfflineScriptureProvider], eliminating external
 * network latency, rate limits, and permissions.
 */
interface BibleApiService {
    suspend fun getChapter(
        passage: String,
        translation: String = "web"
    ): BibleApiResponse

    companion object {
        fun create(): BibleApiService {
            return object : BibleApiService {
                override suspend fun getChapter(
                    passage: String,
                    translation: String
                ): BibleApiResponse {
                    return BibleApiResponse()
                }
            }
        }
    }
}
