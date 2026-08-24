package com.tustockpro.booklibrary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookKey: String,
    val text: String,
    val photoUri: String?,
    val createdAt: Long
)
