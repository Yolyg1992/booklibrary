package com.tustockpro.booklibrary.domain.model

data class Book(
    val bookId: String,
    val title: String,
    val authors: List<String> = emptyList(),
    val description: String? = null,
    val coverUrl: String? = null,
    val categories: List<String> = emptyList(),
    val averageRating: Double? = null,
    val isAvailable: Boolean = false,
    val isbn: String? = null,
    val publishedDate: String? = null,
    val infoLink: String? = null,
    val isFavorite: Boolean = false,
    val addedAt: Long? = null
)
