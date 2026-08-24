package com.tustockpro.booklibrary.data.repository

import com.tustockpro.booklibrary.data.local.dao.FavoriteBookDao
import com.tustockpro.booklibrary.data.local.dao.ReviewDao
import com.tustockpro.booklibrary.data.local.entity.FavoriteBookEntity
import com.tustockpro.booklibrary.data.local.entity.ReviewEntity
import com.tustockpro.booklibrary.data.remote.GoogleBooksApi
import com.tustockpro.booklibrary.data.remote.dto.GoogleBookItemDto
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.domain.model.BookSearchField
import com.tustockpro.booklibrary.domain.model.BookSearchRequest
import com.tustockpro.booklibrary.domain.model.Review
import com.tustockpro.booklibrary.domain.repository.BookRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

class BookRepositoryImpl(
    private val api: GoogleBooksApi,
    private val favoriteBookDao: FavoriteBookDao,
    private val reviewDao: ReviewDao
) : BookRepository {

    override suspend fun searchBooks(
        request: BookSearchRequest
    ): Result<List<Book>> {
        val favoriteIds =
            favoriteBookDao.getFavoriteIds().toSet()

        return try {
            val remoteBooks =
                api.searchBooks(
                    query = buildQuery(request),
                    maxResults = request.maxResults
                ).items.mapNotNull { item ->
                    item.id?.let { id ->
                        item.toBookDomain(
                            isFavorite = id in favoriteIds
                        )
                    }
                }
            val localBooks =
                sampleBooks(
                    request = request,
                    favoriteIds = favoriteIds
                )

            Result.success(
                (localBooks + remoteBooks)
                    .distinctBy { book ->
                        book.bookId
                    }
            )
        } catch (exception: IOException) {
            Result.success(
                sampleBooks(
                    request = request,
                    favoriteIds = favoriteIds
                )
            )
        } catch (exception: HttpException) {
            Result.success(
                sampleBooks(
                    request = request,
                    favoriteIds = favoriteIds
                )
            )
        }
    }

    override suspend fun getBook(
        key: String
    ): Result<Book> {
        val localFavorite =
            favoriteBookDao.getFavorite(key)

        return safeApiCall {
            api.getVolume(key).toBookDomain(
                isFavorite = localFavorite != null
            )
        }.recoverCatching {
            sampleBooks().firstOrNull { book ->
                book.bookId == key
            }?.copy(
                isFavorite = localFavorite != null
            )
                ?: localFavorite?.toStoredBookDomain()
                ?: throw it
        }
    }

    override fun observeFavorite(
        key: String
    ): Flow<Boolean> {
        return favoriteBookDao.observeFavorite(key)
    }

    override fun observeFavoriteIds(): Flow<Set<String>> {
        return favoriteBookDao.observeFavoriteIds().map {
            it.toSet()
        }
    }

    override fun observeFavorites(): Flow<List<Book>> {
        return favoriteBookDao.observeAll().map { list ->
            list.map { entity ->
                entity.toStoredBookDomain()
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
                    bookId = book.bookId,
                    title = book.title,
                    cover = book.coverUrl,
                    addedAt = System.currentTimeMillis()
                )
            )

        } else {
            favoriteBookDao.deleteById(book.bookId)
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

    private fun buildQuery(
        request: BookSearchRequest
    ): String {
        val query =
            request.query.trim()

        val category =
            request.category?.trim().orEmpty()

        val baseQuery =
            when {
                query.isBlank() && category.isNotBlank() ->
                    "subject:$category"
                query.isBlank() -> "subject:fiction"
                request.field == BookSearchField.TITLE ->
                    "intitle:$query"
                request.field == BookSearchField.AUTHOR ->
                    "inauthor:$query"
                request.field == BookSearchField.CATEGORY ->
                    "subject:$query"
                request.field == BookSearchField.ISBN ->
                    "isbn:$query"
                else -> query
            }

        return if (
            category.isNotBlank() &&
            request.field != BookSearchField.CATEGORY &&
            !baseQuery.contains("subject:$category")
        ) {
            "$baseQuery subject:$category"
        } else {
            baseQuery
        }
    }

    private suspend fun <T> safeApiCall(
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (exception: IOException) {
            Result.failure(exception)
        } catch (exception: HttpException) {
            Result.failure(exception)
        }
    }

    private fun sampleBooks(
        request: BookSearchRequest? = null,
        favoriteIds: Set<String> = emptySet()
    ): List<Book> {
        val allBooks =
            listOf(
                Book(
                    bookId = "sample-android",
                    title = "Clean Code",
                    authors = listOf("Robert C. Martin"),
                    description = "Libro de referencia para buenas practicas de desarrollo y lectura tecnica.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9780132350884-L.jpg",
                    categories = listOf("technology"),
                    averageRating = 4.5,
                    isAvailable = true,
                    isbn = "9780132350884"
                ),
                Book(
                    bookId = "sample-fantasy",
                    title = "The Hobbit",
                    authors = listOf("J. R. R. Tolkien"),
                    description = "Clasico de fantasia que sirve como muestra visible para el catalogo local.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9780261103344-L.jpg",
                    categories = listOf("fantasy"),
                    averageRating = 4.2,
                    isAvailable = true,
                    isbn = "9780261103344"
                ),
                Book(
                    bookId = "sample-romance",
                    title = "Pride and Prejudice",
                    authors = listOf("Jane Austen"),
                    description = "Novela clasica de romance para mostrar resultados con portada estable.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9780141439518-L.jpg",
                    categories = listOf("romance"),
                    averageRating = 3.9,
                    isAvailable = true,
                    isbn = "9780141439518"
                ),
                Book(
                    bookId = "sample-science",
                    title = "A Brief History of Time",
                    authors = listOf("Stephen Hawking"),
                    description = "Introduccion conocida a la ciencia y el universo para el filtro de categoria.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9780553380163-L.jpg",
                    categories = listOf("science"),
                    averageRating = 4.0,
                    isAvailable = false,
                    isbn = "9780553380163"
                ),
                Book(
                    bookId = "sample-history",
                    title = "Sapiens",
                    authors = listOf("Yuval Noah Harari"),
                    description = "Titulo popular para mantener visible una opcion historica cuando falla la API.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9780062316097-L.jpg",
                    categories = listOf("history"),
                    averageRating = 4.1,
                    isAvailable = true,
                    isbn = "9780062316097"
                ),
                Book(
                    bookId = "sample-technology-2",
                    title = "Kotlin in Action",
                    authors = listOf("Dmitry Jemerov", "Svetlana Isakova"),
                    description = "Opcion adicional para tecnologia y busqueda por autor o titulo.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9781617293290-L.jpg",
                    categories = listOf("technology"),
                    averageRating = 4.4,
                    isAvailable = true,
                    isbn = "9781617293290"
                ),
                Book(
                    bookId = "sample-fantasy-2",
                    title = "Harry Potter and the Philosopher's Stone",
                    authors = listOf("J. K. Rowling"),
                    description = "Otra referencia conocida para reforzar el filtro de fantasia.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9780747532699-L.jpg",
                    categories = listOf("fantasy"),
                    averageRating = 4.6,
                    isAvailable = true,
                    isbn = "9780747532699"
                ),
                Book(
                    bookId = "sample-romance-2",
                    title = "Jane Eyre",
                    authors = listOf("Charlotte Bronte"),
                    description = "Clasico adicional para que romance tenga mas de un resultado.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9780141441146-L.jpg",
                    categories = listOf("romance"),
                    averageRating = 4.1,
                    isAvailable = true,
                    isbn = "9780141441146"
                ),
                Book(
                    bookId = "sample-science-2",
                    title = "Cosmos",
                    authors = listOf("Carl Sagan"),
                    description = "Refuerzo del filtro de ciencia con una portada publica estable.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9780345331359-L.jpg",
                    categories = listOf("science"),
                    averageRating = 4.7,
                    isAvailable = true,
                    isbn = "9780345331359"
                ),
                Book(
                    bookId = "sample-history-2",
                    title = "Guns, Germs, and Steel",
                    authors = listOf("Jared Diamond"),
                    description = "Resultado extra para historia y pruebas de busqueda.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9780393317558-L.jpg",
                    categories = listOf("history"),
                    averageRating = 4.0,
                    isAvailable = false,
                    isbn = "9780393317558"
                ),
                Book(
                    bookId = "sample-travel-1",
                    title = "On the Road",
                    authors = listOf("Jack Kerouac"),
                    description = "Libro base para cubrir la categoria de viajes en los filtros.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9780141182674-L.jpg",
                    categories = listOf("travel"),
                    averageRating = 3.8,
                    isAvailable = true,
                    isbn = "9780141182674"
                ),
                Book(
                    bookId = "sample-travel-2",
                    title = "Into the Wild",
                    authors = listOf("Jon Krakauer"),
                    description = "Resultado adicional para la categoria de viajes.",
                    coverUrl = "https://covers.openlibrary.org/b/isbn/9780385486804-L.jpg",
                    categories = listOf("travel"),
                    averageRating = 4.0,
                    isAvailable = true,
                    isbn = "9780385486804"
                )
            )

        val query =
            request?.query.orEmpty().trim()
        val category =
            request?.category.orEmpty().trim()

        return allBooks
            .filter { book ->
                val matchesQuery =
                    query.isBlank() ||
                        book.title.contains(query, ignoreCase = true) ||
                        book.authors.any {
                            it.contains(query, ignoreCase = true)
                        } ||
                        book.categories.any {
                            it.contains(query, ignoreCase = true)
                        } ||
                        book.isbn?.contains(query, ignoreCase = true) == true

                val matchesCategory =
                    category.isBlank() ||
                        book.categories.any {
                            it.contains(category, ignoreCase = true)
                        }

                matchesQuery && matchesCategory
            }
            .map { book ->
                book.copy(
                    isFavorite = book.bookId in favoriteIds
                )
            }
    }

    private fun GoogleBookItemDto.toBookDomain(
        isFavorite: Boolean
    ): Book {
        val volumeInfo =
            volumeInfo

        val isbn =
            volumeInfo
                ?.industryIdentifiers
                ?.firstOrNull {
                    it.type == "ISBN_13"
                }
                ?.identifier
                ?: volumeInfo
                    ?.industryIdentifiers
                    ?.firstOrNull {
                        it.type == "ISBN_10"
                    }
                    ?.identifier

        val cover =
            volumeInfo
                ?.imageLinks
                ?.thumbnail
                ?.replace("http://", "https://")
                ?: volumeInfo
                    ?.imageLinks
                    ?.smallThumbnail
                    ?.replace("http://", "https://")

        val available =
            saleInfo?.saleability != "NOT_FOR_SALE" ||
                accessInfo?.epub?.isAvailable == true ||
                accessInfo?.pdf?.isAvailable == true

        return Book(
            bookId = id.orEmpty(),
            title = volumeInfo?.title ?: "Sin titulo",
            authors = volumeInfo?.authors.orEmpty(),
            description = volumeInfo?.description,
            coverUrl = cover,
            categories = volumeInfo?.categories.orEmpty(),
            averageRating = volumeInfo?.averageRating,
            isAvailable = available,
            isbn = isbn,
            publishedDate = volumeInfo?.publishedDate,
            infoLink = volumeInfo?.infoLink,
            isFavorite = isFavorite
        )
    }

    private fun FavoriteBookEntity.toStoredBookDomain(): Book {
        return Book(
            bookId = bookId,
            title = title,
            coverUrl = cover,
            isFavorite = true,
            addedAt = addedAt
        )
    }
}
