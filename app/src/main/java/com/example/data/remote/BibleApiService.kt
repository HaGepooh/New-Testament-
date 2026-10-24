package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class BibleApiResponse(
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "verses") val verses: List<BibleApiVerseDto>? = null,
    @Json(name = "text") val text: String? = null,
    @Json(name = "translation_id") val translationId: String? = null
)

data class BibleApiVerseDto(
    @Json(name = "book_id") val bookId: String? = null,
    @Json(name = "book_name") val bookName: String? = null,
    @Json(name = "chapter") val chapter: Int? = null,
    @Json(name = "verse") val verse: Int? = null,
    @Json(name = "text") val text: String? = null
)

interface BibleApiService {
    @GET("{passage}")
    suspend fun getChapter(
        @Path(value = "passage", encoded = true) passage: String,
        @Query("translation") translation: String = "web"
    ): BibleApiResponse

    companion object {
        fun create(): BibleApiService {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://bible-api.com/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(BibleApiService::class.java)
        }
    }
}
