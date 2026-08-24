package com.tustockpro.booklibrary.data.remote

import com.tustockpro.booklibrary.data.remote.dto.GoogleBookItemDto
import com.tustockpro.booklibrary.data.remote.dto.GoogleBooksVolumesResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApi {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20
    ): GoogleBooksVolumesResponse

    @GET("volumes/{id}")
    suspend fun getVolume(
        @Path("id") id: String
    ): GoogleBookItemDto

    companion object {
        private const val BASE_URL =
            "https://www.googleapis.com/books/v1/"

        fun create(): GoogleBooksApi {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()
                .create(GoogleBooksApi::class.java)
        }
    }
}
