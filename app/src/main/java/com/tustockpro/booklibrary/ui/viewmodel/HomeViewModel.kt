package com.tustockpro.booklibrary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.domain.model.BookSearchField
import com.tustockpro.booklibrary.domain.model.BookSearchRequest
import com.tustockpro.booklibrary.domain.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AvailabilityFilter {
    ALL,
    AVAILABLE,
    UNAVAILABLE
}

enum class RatingFilter(
    val minimumRating: Double?
) {
    ALL(null),
    THREE_PLUS(3.0),
    FOUR_PLUS(4.0)
}

class HomeViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val favoriteIds =
        repository.observeFavoriteIds()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet()
            )

    private val remoteState =
        MutableStateFlow<UiState<List<Book>>>(
            UiState.Loading
        )

    private val _query =
        MutableStateFlow("")

    private var lastSearchQuery: String = ""

    val query: StateFlow<String> =
        _query.asStateFlow()

    private val _searchField =
        MutableStateFlow(BookSearchField.GENERAL)

    val searchField: StateFlow<BookSearchField> =
        _searchField.asStateFlow()

    private val _selectedCategory =
        MutableStateFlow<String?>(null)

    val selectedCategory: StateFlow<String?> =
        _selectedCategory.asStateFlow()

    private val _availabilityFilter =
        MutableStateFlow(AvailabilityFilter.ALL)

    val availabilityFilter: StateFlow<AvailabilityFilter> =
        _availabilityFilter.asStateFlow()

    private val _ratingFilter =
        MutableStateFlow(RatingFilter.ALL)

    val ratingFilter: StateFlow<RatingFilter> =
        _ratingFilter.asStateFlow()

    val uiState: StateFlow<UiState<List<Book>>> =
        combine(
            remoteState,
            favoriteIds,
            selectedCategory,
            availabilityFilter,
            ratingFilter
        ) { state, favorites, category, availability, rating ->
            when (state) {
                UiState.Loading -> UiState.Loading
                is UiState.Error -> state
                is UiState.Success -> {
                    val filtered =
                        state.data
                            .map { book ->
                                book.copy(
                                    isFavorite =
                                        book.bookId in favorites
                                )
                            }
                            .filter { book ->
                                matchesCategory(
                                    book = book,
                                    category = category
                                ) &&
                                    matchesAvailability(
                                        book = book,
                                        filter = availability
                                    ) &&
                                    matchesRating(
                                        book = book,
                                        filter = rating
                                    )
                            }
                    UiState.Success(filtered)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    init {
        search()
    }

    fun updateQuery(value: String) {
        _query.value = value
    }

    fun clearQuery() {
        _query.value = ""
    }

    fun selectSearchField(field: BookSearchField) {
        _searchField.value = field
        if (query.value.isNotBlank()) {
            search()
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
        search()
    }

    fun selectAvailabilityFilter(filter: AvailabilityFilter) {
        _availabilityFilter.value = filter
    }

    fun selectRatingFilter(filter: RatingFilter) {
        _ratingFilter.value = filter
    }

    fun searchByIsbn(isbn: String) {
        _query.value = isbn
        _searchField.value = BookSearchField.ISBN
        search()
    }

    fun search() {
        viewModelScope.launch {
            remoteState.value = UiState.Loading

            val normalizedQuery = _query.value.trim()
            val searchText = if (normalizedQuery.isNotBlank()) {
                normalizedQuery
            } else {
                lastSearchQuery
            }

            if (normalizedQuery.isNotBlank()) {
                lastSearchQuery = normalizedQuery
            }

            _query.value = ""

            val request = buildRequest(searchText)

            remoteState.value =
                repository.searchBooks(request).fold(
                    onSuccess = { books ->
                        UiState.Success(books)
                    },
                    onFailure = { error ->
                        UiState.Error(
                            error.message
                                ?: "No se pudo consultar Google Books."
                        )
                    }
                )
        }
    }

    fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            repository.setFavorite(
                book = book,
                favorite = !book.isFavorite
            )

            remoteState.update { state ->
                if (state is UiState.Success) {
                    UiState.Success(
                        state.data.map { current ->
                            if (current.bookId == book.bookId) {
                                current.copy(
                                    isFavorite = !book.isFavorite
                                )
                            } else {
                                current
                            }
                        }
                    )
                } else {
                    state
                }
            }
        }
    }

    private fun buildRequest(searchText: String = query.value): BookSearchRequest {
        val currentQuery =
            searchText.trim()
        val category =
            selectedCategory.value

        return if (
            currentQuery.isBlank() &&
            !category.isNullOrBlank()
        ) {
            BookSearchRequest(
                query = category,
                field = BookSearchField.CATEGORY
            )
        } else {
            BookSearchRequest(
                query = currentQuery,
                field = searchField.value,
                category = category
            )
        }
    }

    private fun matchesCategory(
        book: Book,
        category: String?
    ): Boolean {
        return category.isNullOrBlank() ||
            book.categories.any {
                it.contains(category, ignoreCase = true)
            }
    }

    private fun matchesAvailability(
        book: Book,
        filter: AvailabilityFilter
    ): Boolean {
        return when (filter) {
            AvailabilityFilter.ALL -> true
            AvailabilityFilter.AVAILABLE -> book.isAvailable
            AvailabilityFilter.UNAVAILABLE -> !book.isAvailable
        }
    }

    private fun matchesRating(
        book: Book,
        filter: RatingFilter
    ): Boolean {
        val minimum = filter.minimumRating ?: return true
        return (book.averageRating ?: 0.0) >= minimum
    }

    class Factory(
        private val repository: BookRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            return HomeViewModel(
                repository = repository
            ) as T
        }
    }
}
