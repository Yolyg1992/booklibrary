package com.tustockpro.booklibrary

import android.app.Application
import com.tustockpro.booklibrary.data.local.BookDatabase
import com.tustockpro.booklibrary.data.remote.GoogleBooksApi
import com.tustockpro.booklibrary.data.repository.BookRepositoryImpl
import com.tustockpro.booklibrary.domain.repository.BookRepository
import com.tustockpro.booklibrary.preferences.UserPreferences

class BookLibraryApplication : Application() {

    val database: BookDatabase by lazy {
        BookDatabase.getInstance(this)
    }

    val userPreferences: UserPreferences by lazy {
        UserPreferences(this)
    }

    val repository: BookRepository by lazy {
        BookRepositoryImpl(
            api = GoogleBooksApi.create(),
            favoriteBookDao = database.favoriteBookDao(),
            reviewDao = database.reviewDao()
        )
    }
}
