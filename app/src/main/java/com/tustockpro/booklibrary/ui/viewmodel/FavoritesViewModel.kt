package com.tustockpro.booklibrary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.domain.repository.BookRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: BookRepository
) : ViewModel() {

    val favorites: StateFlow<List<Book>> =
        repository.observeFavorites()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun removeFavorite(book: Book) {
        viewModelScope.launch {
            repository.setFavorite(
                book = book,
                favorite = false
            )
        }
    }

    class Factory(
        private val repository: BookRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            return FavoritesViewModel(
                repository = repository
            ) as T
        }
    }
}
