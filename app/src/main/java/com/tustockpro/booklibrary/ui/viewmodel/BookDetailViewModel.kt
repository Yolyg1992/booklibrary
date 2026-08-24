package com.tustockpro.booklibrary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.domain.model.Review
import com.tustockpro.booklibrary.domain.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val repository: BookRepository,
    private val initialBook: Book
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<Book>>(
            UiState.Success(initialBook)
        )

    val uiState: StateFlow<UiState<Book>> =
        _uiState.asStateFlow()

    val isFavorite: StateFlow<Boolean> =
        repository.observeFavorite(initialBook.bookId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = initialBook.isFavorite
            )

    val reviews: StateFlow<List<Review>> =
        repository.observeReviews(initialBook.bookId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        loadBook()
    }

    fun loadBook() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value =
                repository.getBook(initialBook.bookId).fold(
                    onSuccess = {
                        UiState.Success(
                            it.copy(
                                isFavorite =
                                    isFavorite.value || it.isFavorite
                            )
                        )
                    },
                    onFailure = {
                        UiState.Error(
                            it.message
                                ?: "No se pudo cargar el detalle."
                        )
                    }
                )
        }
    }

    fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            repository.setFavorite(
                book = book,
                favorite = !isFavorite.value
            )
        }
    }

    fun addReview(text: String, photoUri: String?) {
        val cleanText = text.trim()
        if (cleanText.isBlank() && photoUri.isNullOrBlank()) {
            return
        }

        viewModelScope.launch {
            repository.addReview(
                bookKey = initialBook.bookId,
                text = cleanText,
                photoUri = photoUri
            )
        }
    }

    class Factory(
        private val repository: BookRepository,
        private val initialBook: Book
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            return BookDetailViewModel(
                repository = repository,
                initialBook = initialBook
            ) as T
        }
    }
}
