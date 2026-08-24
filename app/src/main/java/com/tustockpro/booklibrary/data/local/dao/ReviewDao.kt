package com.tustockpro.booklibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tustockpro.booklibrary.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Query(
        "SELECT * FROM reviews WHERE bookKey = :bookKey " +
        "ORDER BY createdAt DESC"
    )
    fun observeByBook(bookKey: String): Flow<List<ReviewEntity>>

    @Insert
    suspend fun insert(review: ReviewEntity)
}
