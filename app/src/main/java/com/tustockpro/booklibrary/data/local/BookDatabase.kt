package com.tustockpro.booklibrary.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tustockpro.booklibrary.data.local.dao.FavoriteBookDao
import com.tustockpro.booklibrary.data.local.dao.ReviewDao
import com.tustockpro.booklibrary.data.local.entity.FavoriteBookEntity
import com.tustockpro.booklibrary.data.local.entity.ReviewEntity

@Database(
    entities = [
        FavoriteBookEntity::class,
        ReviewEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BookDatabase : RoomDatabase() {

    abstract fun favoriteBookDao(): FavoriteBookDao

    abstract fun reviewDao(): ReviewDao

    companion object {

        @Volatile
        private var INSTANCE: BookDatabase? = null

        fun getInstance(context: Context): BookDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "booklibrary.db"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also {
                    INSTANCE = it
                }
            }
        }
    }
}
