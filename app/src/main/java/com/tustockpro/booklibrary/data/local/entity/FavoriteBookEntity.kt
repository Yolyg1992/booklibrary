package com.tustockpro.booklibrary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteBookEntity(
    @PrimaryKey
    val bookId: String,
    val title: String,
    val cover: String?,
    val addedAt: Long
)
