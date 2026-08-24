package com.tustockpro.booklibrary.domain.model

data class BookSearchRequest(
    val query: String,
    val field: BookSearchField = BookSearchField.GENERAL,
    val category: String? = null,
    val maxResults: Int = 20
)
