package com.tustockpro.booklibrary.ui.screens.detail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tustockpro.booklibrary.ui.viewmodel.BookDetailViewModel
import com.tustockpro.booklibrary.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: BookDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    var reviewText by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedPhotoUri = uri
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Detalle")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val currentState = uiState) {
            UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cargando detalle del libro...")
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = currentState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = viewModel::loadBook) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            is UiState.Success -> {
                val book = currentState.data

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 12.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme
                                            .surface
                                    )
                                    .padding(20.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(16.dp)
                            ) {
                                if (book.coverUrl.isNullOrBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(320.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                RoundedCornerShape(24.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.AutoStories,
                                            contentDescription = "Portada no disponible",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    AsyncImage(
                                        model = book.coverUrl,
                                        contentDescription = book.title,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(320.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }

                                Column(
                                    verticalArrangement =
                                        Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = book.title,
                                        style = MaterialTheme
                                            .typography
                                            .headlineMedium
                                    )

                                    Text(
                                        text = book.authors.joinToString(
                                            ", "
                                        ).ifBlank {
                                            "Autor por confirmar"
                                        },
                                        style = MaterialTheme
                                            .typography
                                            .titleMedium
                                    )

                                    Text(
                                        text =
                                            if (book.isAvailable) {
                                                "Disponible para consulta"
                                            } else {
                                                "Solo informacion bibliografica"
                                            }
                                    )

                                    book.averageRating?.let { rating ->
                                        Text(
                                            text = "Valoracion: ${"%.1f".format(rating)} / 5"
                                        )
                                    }

                                    if (book.categories.isNotEmpty()) {
                                        androidx.compose.foundation.layout.FlowRow(
                                            horizontalArrangement =
                                                Arrangement.spacedBy(8.dp),
                                            verticalArrangement =
                                                Arrangement.spacedBy(8.dp)
                                        ) {
                                            book.categories.forEach { category ->
                                                AssistChip(
                                                    onClick = {},
                                                    label = {
                                                        Text(category)
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.toggleFavorite(book)
                                        }
                                    ) {
                                        Icon(
                                            imageVector =
                                                if (isFavorite) {
                                                    Icons.Filled.Favorite
                                                } else {
                                                    Icons.Outlined.FavoriteBorder
                                                },
                                            contentDescription = "Favorito",
                                            tint =
                                                MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 8.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Resumen",
                                    style = MaterialTheme
                                        .typography
                                        .titleLarge
                                )

                                Text(
                                    text = book.description
                                        ?: "Google Books no devolvio una descripcion para este titulo."
                                )

                                book.isbn?.let { isbn ->
                                    Text("ISBN: $isbn")
                                }

                                book.publishedDate?.let { publishedDate ->
                                    Text("Publicado: $publishedDate")
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 8.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Comentarios",
                                    style = MaterialTheme.typography.titleLarge
                                )

                                OutlinedTextField(
                                    value = reviewText,
                                    onValueChange = { reviewText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Escribe tu comentario") }
                                )

                                Button(
                                    onClick = {
                                        val photoUri = selectedPhotoUri?.toString()
                                        viewModel.addReview(reviewText, photoUri)
                                        reviewText = ""
                                        selectedPhotoUri = null
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Guardar comentario")
                                }

                                Button(
                                    onClick = {
                                        photoPicker.launch("image/*")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        if (selectedPhotoUri == null) {
                                            "Añadir foto"
                                        } else {
                                            "Cambiar foto"
                                        }
                                    )
                                }

                                if (selectedPhotoUri != null) {
                                    AsyncImage(
                                        model = selectedPhotoUri,
                                        contentDescription = "Foto del comentario",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                if (reviews.isNotEmpty()) {
                                    reviews.forEach { review ->
                                        Card(
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                if (review.text.isNotBlank()) {
                                                    Text(review.text)
                                                }
                                                if (!review.photoUri.isNullOrBlank()) {
                                                    AsyncImage(
                                                        model = review.photoUri,
                                                        contentDescription = "Foto del usuario",
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(150.dp),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
