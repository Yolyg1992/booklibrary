package com.tustockpro.booklibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tustockpro.booklibrary.data.local.entity.FavoriteBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteBookDao {

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteBookEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE bookId = :bookId)")
    fun observeFavorite(bookId: String): Flow<Boolean>

    @Query("SELECT bookId FROM favorites")
    fun observeFavoriteIds(): Flow<List<String>>

    @Query("SELECT bookId FROM favorites")
    suspend fun getFavoriteIds(): List<String>

    @Query("SELECT * FROM favorites WHERE bookId = :bookId LIMIT 1")
    suspend fun getFavorite(bookId: String): FavoriteBookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: FavoriteBookEntity)

    @Query("DELETE FROM favorites WHERE bookId = :bookId")
    suspend fun deleteById(bookId: String)
}
