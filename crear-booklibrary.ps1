# ============================================================
# BookLibrary - Generador completo de código
# Proyecto:
# C:\Users\Usuario\Desktop\BookLibrary
#
# Package:
# com.tustockpro.booklibrary
# ============================================================

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "      BOOKLIBRARY - GENERADOR DE CODIGO      " -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host ""

$root = (Get-Location).Path

$javaRoot = Join-Path $root "app\src\main\java\com\tustockpro\booklibrary"
$resRoot = Join-Path $root "app\src\main\res"

Write-Host "Proyecto: $root" -ForegroundColor Yellow
Write-Host ""

# ============================================================
# CREAR DIRECTORIOS
# ============================================================

$directories = @(
    $javaRoot
    (Join-Path $javaRoot "data")
    (Join-Path $javaRoot "data\local")
    (Join-Path $javaRoot "data\local\dao")
    (Join-Path $javaRoot "data\local\entity")
    (Join-Path $javaRoot "data\remote")
    (Join-Path $javaRoot "data\remote\dto")
    (Join-Path $javaRoot "data\repository")
    (Join-Path $javaRoot "domain")
    (Join-Path $javaRoot "domain\model")
    (Join-Path $javaRoot "domain\repository")
    (Join-Path $javaRoot "preferences")
    (Join-Path $javaRoot "ui")
    (Join-Path $javaRoot "ui\components")
    (Join-Path $javaRoot "ui\navigation")
    (Join-Path $javaRoot "ui\screens")
    (Join-Path $javaRoot "ui\screens\home")
    (Join-Path $javaRoot "ui\screens\detail")
    (Join-Path $javaRoot "ui\screens\settings")
    (Join-Path $javaRoot "ui\theme")
    (Join-Path $javaRoot "ui\viewmodel")
    (Join-Path $resRoot "values")
    (Join-Path $resRoot "xml")
)

foreach ($directory in $directories) {
    New-Item -ItemType Directory -Force -Path $directory | Out-Null
}

Write-Host "Directorios creados." -ForegroundColor Green

# ============================================================
# FUNCION PARA ESCRIBIR ARCHIVOS
# ============================================================

function Write-ProjectFile {
    param(
        [string]$Path,
        [string]$Content
    )

    $parent = Split-Path $Path -Parent

    if (-not (Test-Path $parent)) {
        New-Item -ItemType Directory -Force -Path $parent | Out-Null
    }

    Set-Content -Path $Path -Value $Content -Encoding UTF8

    Write-Host "CREADO: $Path" -ForegroundColor DarkGreen
}

# ============================================================
# BookLibraryApplication.kt
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "BookLibraryApplication.kt") `
@'
package com.tustockpro.booklibrary

import android.app.Application
import com.tustockpro.booklibrary.data.local.BookDatabase
import com.tustockpro.booklibrary.data.remote.OpenLibraryApi
import com.tustockpro.booklibrary.data.repository.BookRepositoryImpl
import com.tustockpro.booklibrary.preferences.UserPreferences

class BookLibraryApplication : Application() {

    val database: BookDatabase by lazy {
        BookDatabase.getInstance(this)
    }

    val userPreferences: UserPreferences by lazy {
        UserPreferences(this)
    }

    val repository: BookRepositoryImpl by lazy {
        BookRepositoryImpl(
            api = OpenLibraryApi.create(),
            favoriteBookDao = database.favoriteBookDao(),
            reviewDao = database.reviewDao()
        )
    }
}
'@

# ============================================================
# MainActivity.kt
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "MainActivity.kt") `
@'
package com.tustockpro.booklibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tustockpro.booklibrary.ui.navigation.AppNavigation
import com.tustockpro.booklibrary.ui.theme.BookLibraryTheme
import com.tustockpro.booklibrary.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val application =
            application as BookLibraryApplication

        setContent {

            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(
                    application.userPreferences
                )
            )

            val darkTheme by settingsViewModel.darkTheme
                .collectAsState(initial = false)

            BookLibraryTheme(
                darkTheme = darkTheme
            ) {
                AppNavigation(
                    application = application,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
'@

# ============================================================
# DOMAIN MODEL - Book.kt
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "domain\model\Book.kt") `
@'
package com.tustockpro.booklibrary.domain.model

data class Book(
    val key: String,
    val title: String,
    val authors: List<String>,
    val firstPublishYear: Int?,
    val coverUrl: String?,
    val description: String? = null
)
'@

# ============================================================
# DOMAIN MODEL - Review.kt
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "domain\model\Review.kt") `
@'
package com.tustockpro.booklibrary.domain.model

data class Review(
    val id: Long,
    val bookKey: String,
    val text: String,
    val photoUri: String?,
    val createdAt: Long
)
'@

# ============================================================
# DOMAIN REPOSITORY
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "domain\repository\BookRepository.kt") `
@'
package com.tustockpro.booklibrary.domain.repository

import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    suspend fun searchBooks(query: String): Result<List<Book>>

    suspend fun getBook(key: String): Result<Book>

    fun observeFavorite(key: String): Flow<Boolean>

    fun observeFavorites(): Flow<List<Book>>

    suspend fun setFavorite(book: Book, favorite: Boolean)

    fun observeReviews(bookKey: String): Flow<List<Review>>

    suspend fun addReview(
        bookKey: String,
        text: String,
        photoUri: String?
    )
}
'@

# ============================================================
# LOCAL ENTITY - FavoriteBookEntity.kt
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "data\local\entity\FavoriteBookEntity.kt") `
@'
package com.tustockpro.booklibrary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_books")
data class FavoriteBookEntity(
    @PrimaryKey
    val key: String,
    val title: String,
    val authors: String,
    val firstPublishYear: Int?,
    val coverUrl: String?
)
'@

# ============================================================
# LOCAL ENTITY - ReviewEntity.kt
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "data\local\entity\ReviewEntity.kt") `
@'
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
'@

# ============================================================
# DAO FAVORITES
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "data\local\dao\FavoriteBookDao.kt") `
@'
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

    @Query("SELECT * FROM favorite_books ORDER BY title ASC")
    fun observeAll(): Flow<List<FavoriteBookEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_books WHERE `key` = :key)")
    fun observeFavorite(key: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: FavoriteBookEntity)

    @Delete
    suspend fun delete(book: FavoriteBookEntity)

    @Query("DELETE FROM favorite_books WHERE `key` = :key")
    suspend fun deleteByKey(key: String)
}
'@

# ============================================================
# DAO REVIEWS
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "data\local\dao\ReviewDao.kt") `
@'
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
'@

# ============================================================
# ROOM DATABASE
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "data\local\BookDatabase.kt") `
@'
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
    version = 1,
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
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
'@

# ============================================================
# REMOTE DTO
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "data\remote\dto\OpenLibraryDtos.kt") `
@'
package com.tustockpro.booklibrary.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenLibrarySearchResponse(
    @SerializedName("docs")
    val docs: List<OpenLibraryBookDto> = emptyList()
)

data class OpenLibraryBookDto(
    @SerializedName("key")
    val key: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("author_name")
    val authorName: List<String>?,

    @SerializedName("first_publish_year")
    val firstPublishYear: Int?,

    @SerializedName("cover_i")
    val coverId: Int?
)

data class OpenLibraryWorkDto(
    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: Any?,

    @SerializedName("covers")
    val covers: List<Int>?
)
'@

# ============================================================
# OPEN LIBRARY API
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "data\remote\OpenLibraryApi.kt") `
@'
package com.tustockpro.booklibrary.data.remote

import com.tustockpro.booklibrary.data.remote.dto.OpenLibrarySearchResponse
import com.tustockpro.booklibrary.data.remote.dto.OpenLibraryWorkDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApi {

    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): OpenLibrarySearchResponse

    @GET("works/{id}.json")
    suspend fun getWork(
        @Path("id") id: String
    ): OpenLibraryWorkDto

    companion object {

        private const val BASE_URL =
            "https://openlibrary.org/"

        fun create(): OpenLibraryApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()
                .create(OpenLibraryApi::class.java)
        }
    }
}
'@

# ============================================================
# REPOSITORY IMPLEMENTATION
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "data\repository\BookRepositoryImpl.kt") `
@'
package com.tustockpro.booklibrary.data.repository

import com.tustockpro.booklibrary.data.local.dao.FavoriteBookDao
import com.tustockpro.booklibrary.data.local.dao.ReviewDao
import com.tustockpro.booklibrary.data.local.entity.FavoriteBookEntity
import com.tustockpro.booklibrary.data.local.entity.ReviewEntity
import com.tustockpro.booklibrary.data.remote.OpenLibraryApi
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.domain.model.Review
import com.tustockpro.booklibrary.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepositoryImpl(
    private val api: OpenLibraryApi,
    private val favoriteBookDao: FavoriteBookDao,
    private val reviewDao: ReviewDao
) : BookRepository {

    override suspend fun searchBooks(
        query: String
    ): Result<List<Book>> {
        return try {

            val response = api.searchBooks(query)

            val books = response.docs.mapNotNull { dto ->

                val key = dto.key ?: return@mapNotNull null

                val title = dto.title ?: "Sin título"

                val authors =
                    dto.authorName ?: emptyList()

                val coverUrl = dto.coverId?.let {
                    "https://covers.openlibrary.org/b/id/$it-M.jpg"
                }

                Book(
                    key = key,
                    title = title,
                    authors = authors,
                    firstPublishYear =
                        dto.firstPublishYear,
                    coverUrl = coverUrl
                )
            }

            Result.success(books)

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getBook(
        key: String
    ): Result<Book> {

        return try {

            val cleanKey = key
                .removePrefix("/works/")
                .removePrefix("works/")

            val response =
                api.getWork(cleanKey)

            val coverUrl =
                response.covers
                    ?.firstOrNull()
                    ?.let {
                        "https://covers.openlibrary.org/b/id/$it-M.jpg"
                    }

            val description =
                when (val value = response.description) {
                    is String -> value
                    is Map<*, *> ->
                        value["value"]?.toString()
                    else -> null
                }

            Result.success(
                Book(
                    key = "/works/$cleanKey",
                    title = response.title
                        ?: "Sin título",
                    authors = emptyList(),
                    firstPublishYear = null,
                    coverUrl = coverUrl,
                    description = description
                )
            )

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override fun observeFavorite(
        key: String
    ): Flow<Boolean> {
        return favoriteBookDao.observeFavorite(key)
    }

    override fun observeFavorites(): Flow<List<Book>> {
        return favoriteBookDao.observeAll().map { list ->
            list.map {
                Book(
                    key = it.key,
                    title = it.title,
                    authors = if (it.authors.isBlank()) {
                        emptyList()
                    } else {
                        it.authors.split("|||")
                    },
                    firstPublishYear =
                        it.firstPublishYear,
                    coverUrl = it.coverUrl
                )
            }
        }
    }

    override suspend fun setFavorite(
        book: Book,
        favorite: Boolean
    ) {

        if (favorite) {

            favoriteBookDao.insert(
                FavoriteBookEntity(
                    key = book.key,
                    title = book.title,
                    authors = book.authors.joinToString("|||"),
                    firstPublishYear =
                        book.firstPublishYear,
                    coverUrl = book.coverUrl
                )
            )

        } else {
            favoriteBookDao.deleteByKey(book.key)
        }
    }

    override fun observeReviews(
        bookKey: String
    ): Flow<List<Review>> {
        return reviewDao.observeByBook(bookKey).map { list ->
            list.map {
                Review(
                    id = it.id,
                    bookKey = it.bookKey,
                    text = it.text,
                    photoUri = it.photoUri,
                    createdAt = it.createdAt
                )
            }
        }
    }

    override suspend fun addReview(
        bookKey: String,
        text: String,
        photoUri: String?
    ) {
        reviewDao.insert(
            ReviewEntity(
                bookKey = bookKey,
                text = text,
                photoUri = photoUri,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
'@

# ============================================================
# USER PREFERENCES - DATASTORE
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "preferences\UserPreferences.kt") `
@'
package com.tustockpro.booklibrary.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "booklibrary_preferences"
)

class UserPreferences(
    private val context: Context
) {

    companion object {
        private val DARK_THEME =
            booleanPreferencesKey("dark_theme")
    }

    val darkTheme: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[DARK_THEME] ?: false
        }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME] = enabled
        }
    }
}
'@

# ============================================================
# UI STATE
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "ui\viewmodel\UiState.kt") `
@'
package com.tustockpro.booklibrary.ui.viewmodel

sealed interface UiState<out T> {

    data object Loading : UiState<Nothing>

    data class Success<T>(
        val data: T
    ) : UiState<T>

    data class Error(
        val message: String
    ) : UiState<Nothing>
}
'@

# ============================================================
# HOME VIEWMODEL
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "ui\viewmodel\HomeViewModel.kt") `
@'
package com.tustockpro.booklibrary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.domain.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<List<Book>>>(
            UiState.Loading
        )

    val uiState: StateFlow<UiState<List<Book>>> =
        _uiState.asStateFlow()

    private val _query =
        MutableStateFlow("android")

    val query: StateFlow<String> =
        _query.asStateFlow()

    init {
        search("android")
    }

    fun updateQuery(value: String) {
        _query.value = value
    }

    fun search() {
        search(_query.value)
    }

    private fun search(value: String) {

        if (value.isBlank()) return

        viewModelScope.launch {

            _uiState.value = UiState.Loading

            val result =
                repository.searchBooks(value.trim())

            _uiState.value =
                result.fold(
                    onSuccess = {
                        UiState.Success(it)
                    },
                    onFailure = {
                        UiState.Error(
                            it.message
                                ?: "No se pudieron cargar los libros."
                        )
                    }
                )
        }
    }

    class Factory(
        private val repository: BookRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            return HomeViewModel(repository) as T
        }
    }
}
'@

# ============================================================
# DETAIL VIEWMODEL
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "ui\viewmodel\BookDetailViewModel.kt") `
@'
package com.tustockpro.booklibrary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.domain.model.Review
import com.tustockpro.booklibrary.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val repository: BookRepository,
    private val initialBook: Book
) : ViewModel() {

    private val _book =
        MutableStateFlow(initialBook)

    val book: StateFlow<Book> =
        _book.asStateFlow()

    val isFavorite: Flow<Boolean> =
        repository.observeFavorite(initialBook.key)

    val reviews: Flow<List<Review>> =
        repository.observeReviews(initialBook.key)

    fun toggleFavorite() {
        viewModelScope.launch {
            repository.setFavorite(
                book = _book.value,
                favorite = !isFavoriteNow()
            )
        }
    }

    private suspend fun isFavoriteNow(): Boolean {
        var result = false

        repository.observeFavorite(initialBook.key)
            .collect {
                result = it
                return@collect
            }

        return result
    }

    fun addReview(
        text: String,
        photoUri: String?
    ) {
        if (text.isBlank() && photoUri == null) {
            return
        }

        viewModelScope.launch {
            repository.addReview(
                bookKey = _book.value.key,
                text = text.trim(),
                photoUri = photoUri
            )
        }
    }

    class Factory(
        private val repository: BookRepository,
        private val book: Book
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            return BookDetailViewModel(
                repository = repository,
                initialBook = book
            ) as T
        }
    }
}
'@

# ============================================================
# SETTINGS VIEWMODEL
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "ui\viewmodel\SettingsViewModel.kt") `
@'
package com.tustockpro.booklibrary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tustockpro.booklibrary.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: UserPreferences
) : ViewModel() {

    val darkTheme: Flow<Boolean> =
        preferences.darkTheme

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setDarkTheme(enabled)
        }
    }

    class Factory(
        private val preferences: UserPreferences
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            return SettingsViewModel(
                preferences
            ) as T
        }
    }
}
'@

# ============================================================
# BOOK CARD
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "ui\components\BookCard.kt") `
@'
package com.tustockpro.booklibrary.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tustockpro.booklibrary.domain.model.Book

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier.size(
                    width = 80.dp,
                    height = 110.dp
                ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                if (book.authors.isNotEmpty()) {

                    Text(
                        text = book.authors.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                book.firstPublishYear?.let { year ->

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Publicado: $year",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
'@

# ============================================================
# HOME SCREEN
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "ui\screens\home\HomeScreen.kt") `
@'
package com.tustockpro.booklibrary.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.ui.components.BookCard
import com.tustockpro.booklibrary.ui.viewmodel.HomeViewModel
import com.tustockpro.booklibrary.ui.viewmodel.UiState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onBookClick: (Book) -> Unit,
    onSettingsClick: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("BookLibrary")
                },
                actions = {
                    IconButton(
                        onClick = onSettingsClick
                    ) {
                        Text("⚙")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = viewModel::updateQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = {
                    Text("Buscar libros")
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = viewModel::search
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    }
                }
            )

            when (val currentState = state) {

                UiState.Loading -> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Cargando libros...",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is UiState.Success -> {

                    BookList(
                        books = currentState.data,
                        onBookClick = onBookClick
                    )
                }

                is UiState.Error -> {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        Text(
                            text = currentState.message
                        )

                        Button(
                            onClick = viewModel::search,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookList(
    books: List<Book>,
    onBookClick: (Book) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        items(
            items = books,
            key = { it.key }
        ) { book ->

            BookCard(
                book = book,
                onClick = {
                    onBookClick(book)
                }
            )
        }
    }
}
'@

# ============================================================
# BOOK DETAIL SCREEN
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "ui\screens\detail\BookDetailScreen.kt") `
@'
package com.tustockpro.booklibrary.ui.screens.detail

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.ui.viewmodel.BookDetailViewModel
import java.io.File

@Composable
fun BookDetailScreen(
    book: Book,
    viewModel: BookDetailViewModel,
    onBack: () -> Unit
) {

    val isFavorite by viewModel.isFavorite.collectAsState(
        initial = false
    )

    val reviews by viewModel.reviews.collectAsState(
        initial = emptyList()
    )

    var reviewText by remember {
        mutableStateOf("")
    }

    var photoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var pendingCameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    fun createImageUri(context: Context): Uri {

        val directory = File(
            context.cacheDir,
            "review_images"
        )

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(
            directory,
            "review_${System.currentTimeMillis()}.jpg"
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->

            if (success) {
                photoUri = pendingCameraUri
            } else {
                pendingCameraUri = null
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                val uri = createImageUri(context)

                pendingCameraUri = uri

                cameraLauncher.launch(uri)
            }
        }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("Detalle del libro")
                },
                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {

                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite()
                        }
                    ) {

                        Icon(
                            imageVector =
                                if (isFavorite) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                },
                            contentDescription =
                                if (isFavorite) {
                                    "Quitar favorito"
                                } else {
                                    "Agregar favorito"
                                }
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            item {

                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = book.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentScale = ContentScale.Fit
                )
            }

            item {

                Text(
                    text = book.title,
                    style = androidx.compose.material3.MaterialTheme
                        .typography
                        .headlineSmall
                )
            }

            if (book.authors.isNotEmpty()) {

                item {

                    Text(
                        text = "Autor: " +
                            book.authors.joinToString(", ")
                    )
                }
            }

            book.firstPublishYear?.let { year ->

                item {

                    Text(
                        text = "Año de publicación: $year"
                    )
                }
            }

            book.description?.let { description ->

                item {

                    Text(
                        text = description
                    )
                }
            }

            item {

                Text(
                    text = "Mi reseña",
                    style = androidx.compose.material3.MaterialTheme
                        .typography
                        .titleLarge
                )
            }

            item {

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = {
                        reviewText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Escribe tu reseña")
                    },
                    minLines = 4
                )
            }

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Button(
                        onClick = {

                            if (
                                androidx.core.content.ContextCompat
                                    .checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) ==
                                android.content.pm.PackageManager
                                    .PERMISSION_GRANTED
                            ) {

                                val uri =
                                    createImageUri(context)

                                pendingCameraUri = uri

                                cameraLauncher.launch(uri)

                            } else {

                                permissionLauncher.launch(
                                    Manifest.permission.CAMERA
                                )
                            }
                        }
                    ) {

                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null
                        )

                        Text(
                            text = " Foto"
                        )
                    }

                    Button(
                        onClick = {

                            viewModel.addReview(
                                text = reviewText,
                                photoUri =
                                    photoUri?.toString()
                            )

                            reviewText = ""
                            photoUri = null
                        }
                    ) {

                        Text("Guardar reseña")
                    }
                }
            }

            if (photoUri != null) {

                item {

                    Text(
                        text = "Foto capturada correctamente."
                    )
                }
            }

            item {

                Text(
                    text = "Reseñas guardadas",
                    style = androidx.compose.material3.MaterialTheme
                        .typography
                        .titleLarge
                )
            }

            items(
                items = reviews,
                key = { it.id }
            ) { review ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        if (review.text.isNotBlank()) {

                            Text(
                                text = review.text
                            )
                        }

                        if (review.photoUri != null) {

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            AsyncImage(
                                model = review.photoUri,
                                contentDescription =
                                    "Foto de reseña",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale =
                                    ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}
'@

# ============================================================
# SETTINGS SCREEN
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "ui\screens\settings\SettingsScreen.kt") `
@'
package com.tustockpro.booklibrary.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tustockpro.booklibrary.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {

    val darkTheme by viewModel.darkTheme.collectAsState(
        initial = false
    )

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("Configuración")
                },
                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Modo oscuro"
                    )

                    Text(
                        text = "Guardar esta preferencia"
                    )
                }

                Switch(
                    checked = darkTheme,
                    onCheckedChange =
                        viewModel::setDarkTheme
                )
            }
        }
    }
}
'@

# ============================================================
# NAVIGATION
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "ui\navigation\AppNavigation.kt") `
@'
package com.tustockpro.booklibrary.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tustockpro.booklibrary.BookLibraryApplication
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.ui.screens.detail.BookDetailScreen
import com.tustockpro.booklibrary.ui.screens.home.HomeScreen
import com.tustockpro.booklibrary.ui.screens.settings.SettingsScreen
import com.tustockpro.booklibrary.ui.viewmodel.BookDetailViewModel
import com.tustockpro.booklibrary.ui.viewmodel.HomeViewModel
import com.tustockpro.booklibrary.ui.viewmodel.SettingsViewModel

private object Routes {

    const val HOME = "home"

    const val SETTINGS = "settings"

    const val DETAIL =
        "detail/{key}/{title}/{authors}/{year}/{cover}"
}

@Composable
fun AppNavigation(
    application: BookLibraryApplication,
    settingsViewModel: SettingsViewModel
) {

    val navController =
        rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {

        composable(Routes.HOME) {

            val homeViewModel: HomeViewModel =
                viewModel(
                    factory =
                        HomeViewModel.Factory(
                            application.repository
                        )
                )

            HomeScreen(
                viewModel = homeViewModel,

                onBookClick = { book ->

                    val key =
                        Uri.encode(book.key)

                    val title =
                        Uri.encode(book.title)

                    val authors =
                        Uri.encode(
                            book.authors.joinToString("|||")
                        )

                    val year =
                        Uri.encode(
                            book.firstPublishYear?.toString()
                                ?: ""
                        )

                    val cover =
                        Uri.encode(
                            book.coverUrl ?: ""
                        )

                    navController.navigate(
                        "detail/$key/$title/$authors/$year/$cover"
                    )
                },

                onSettingsClick = {

                    navController.navigate(
                        Routes.SETTINGS
                    )
                }
            )
        }

        composable(Routes.SETTINGS) {

            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("key") {
                    type = NavType.StringType
                },
                navArgument("title") {
                    type = NavType.StringType
                },
                navArgument("authors") {
                    type = NavType.StringType
                },
                navArgument("year") {
                    type = NavType.StringType
                },
                navArgument("cover") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val key =
                Uri.decode(
                    backStackEntry.arguments
                        ?.getString("key")
                        .orEmpty()
                )

            val title =
                Uri.decode(
                    backStackEntry.arguments
                        ?.getString("title")
                        .orEmpty()
                )

            val authorsText =
                Uri.decode(
                    backStackEntry.arguments
                        ?.getString("authors")
                        .orEmpty()
                )

            val yearText =
                Uri.decode(
                    backStackEntry.arguments
                        ?.getString("year")
                        .orEmpty()
                )

            val cover =
                Uri.decode(
                    backStackEntry.arguments
                        ?.getString("cover")
                        .orEmpty()
                )

            val book = Book(
                key = key,
                title = title,
                authors =
                    if (authorsText.isBlank()) {
                        emptyList()
                    } else {
                        authorsText.split("|||")
                    },
                firstPublishYear =
                    yearText.toIntOrNull(),
                coverUrl =
                    cover.ifBlank { null }
            )

            val detailViewModel: BookDetailViewModel =
                viewModel(
                    factory =
                        BookDetailViewModel.Factory(
                            application.repository,
                            book
                        )
                )

            BookDetailScreen(
                book = book,
                viewModel = detailViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
'@

# ============================================================
# THEME
# ============================================================

Write-ProjectFile `
    (Join-Path $javaRoot "ui\theme\Theme.kt") `
@'
package com.tustockpro.booklibrary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun BookLibraryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme =
        if (darkTheme) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
'@

# ============================================================
# ANDROID MANIFEST
# ============================================================

Write-ProjectFile `
    (Join-Path $root "app\src\main\AndroidManifest.xml") `
@'
<?xml version="1.0" encoding="utf-8"?>

<manifest
    xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission
        android:name="android.permission.INTERNET" />

    <uses-permission
        android:name="android.permission.CAMERA" />

    <application
        android:name=".BookLibraryApplication"
        android:allowBackup="true"
        android:label="BookLibrary"
        android:supportsRtl="true"
        android:theme="@style/Theme.BookLibrary">

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">

            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />

        </provider>

        <activity
            android:name=".MainActivity"
            android:exported="true">

            <intent-filter>

                <action
                    android:name="android.intent.action.MAIN" />

                <category
                    android:name="android.intent.category.LAUNCHER" />

            </intent-filter>

        </activity>

    </application>

</manifest>
'@

# ============================================================
# FILE PROVIDER PATHS
# ============================================================

Write-ProjectFile `
    (Join-Path $resRoot "xml\file_paths.xml") `
@'
<?xml version="1.0" encoding="utf-8"?>

<paths xmlns:android="http://schemas.android.com/apk/res/android">

    <cache-path
        name="review_images"
        path="review_images/" />

</paths>
'@

# ============================================================
# COLORS
# ============================================================

Write-ProjectFile `
    (Join-Path $resRoot "values\colors.xml") `
@'
<?xml version="1.0" encoding="utf-8"?>

<resources>

    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>

    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>

    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>

</resources>
'@

# ============================================================
# STRINGS
# ============================================================

Write-ProjectFile `
    (Join-Path $resRoot "values\strings.xml") `
@'
<?xml version="1.0" encoding="utf-8"?>

<resources>

    <string name="app_name">BookLibrary</string>

</resources>
'@

# ============================================================
# STYLES
# ============================================================

Write-ProjectFile `
    (Join-Path $resRoot "values\styles.xml") `
@'
<?xml version="1.0" encoding="utf-8"?>

<resources>

    <style
        name="Theme.BookLibrary"
        parent="android:style/Theme.Material.Light.NoActionBar">

        <item name="android:fontFamily">sans</item>

        <item name="android:windowNoTitle">true</item>

        <item name="android:windowActionModeOverlay">true</item>

    </style>

</resources>
'@

# ============================================================
# ELIMINAR themes.xml DUPLICADO SI EXISTE
# ============================================================

$duplicateTheme = Join-Path $resRoot "values\themes.xml"

if (Test-Path $duplicateTheme) {

    Remove-Item $duplicateTheme -Force

    Write-Host ""
    Write-Host "ELIMINADO themes.xml duplicado." -ForegroundColor Yellow
}

# ============================================================
# RESUMEN
# ============================================================

Write-Host ""
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "      GENERACION TERMINADA                   " -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host ""

$files = Get-ChildItem `
    (Join-Path $root "app\src\main") `
    -Recurse `
    -File `
    -ErrorAction SilentlyContinue

$kotlinFiles = $files |
    Where-Object {
        $_.Extension -eq ".kt"
    }

Write-Host "Archivos Kotlin creados: $($kotlinFiles.Count)" -ForegroundColor Green
Write-Host ""

$kotlinFiles |
    Select-Object -ExpandProperty FullName

Write-Host ""
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "IMPORTANTE" -ForegroundColor Yellow
Write-Host "Todavia NO se ha ejecutado Gradle." -ForegroundColor Yellow
Write-Host "El siguiente paso sera compilar y corregir" -ForegroundColor Yellow
Write-Host "cualquier error real del codigo." -ForegroundColor Yellow
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host ""