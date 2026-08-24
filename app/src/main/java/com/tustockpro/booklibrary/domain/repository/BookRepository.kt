package com.tustockpro.booklibrary.domain.repository

import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.domain.model.BookSearchRequest
import com.tustockpro.booklibrary.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    suspend fun searchBooks(
        request: BookSearchRequest
    ): Result<List<Book>>

    suspend fun getBook(key: String): Result<Book>

    fun observeFavorite(key: String): Flow<Boolean>

    fun observeFavoriteIds(): Flow<Set<String>>

    fun observeFavorites(): Flow<List<Book>>

    suspend fun setFavorite(book: Book, favorite: Boolean)

    fun observeReviews(bookKey: String): Flow<List<Review>>

    suspend fun addReview(
        bookKey: String,
        text: String,
        photoUri: String?
    )
}
