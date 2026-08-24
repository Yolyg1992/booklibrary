package com.tustockpro.booklibrary.domain.model

data class Review(
    val id: Long,
    val bookKey: String,
    val text: String,
    val photoUri: String?,
    val createdAt: Long
)
