package com.tustockpro.booklibrary.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.ui.components.BookCard
import com.tustockpro.booklibrary.ui.viewmodel.HomeViewModel
import com.tustockpro.booklibrary.ui.viewmodel.UiState

private data class HomeCategory(
    val label: String,
    val queryValue: String?,
    val icon: ImageVector
)

private val categories = listOf(
    HomeCategory("Todos", null, Icons.Outlined.Category),
    HomeCategory("Fantasía", "fantasy", Icons.Outlined.AutoStories),
    HomeCategory("Romance", "romance", Icons.Outlined.LocalLibrary),
    HomeCategory("Tecnología", "technology", Icons.Outlined.Psychology),
    HomeCategory("Ciencia", "science", Icons.Outlined.Science),
    HomeCategory("Viajes", "travel", Icons.Outlined.TravelExplore)
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    userName: String,
    favoriteGenre: String,
    sessionActive: Boolean,
    onOpenSearch: (String?) -> Unit,
    onBookClick: (Book) -> Unit,
    onFavoriteToggle: (Book) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(30.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 14.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text =
                            if (sessionActive) {
                                "Hola, $userName"
                            } else {
                                "Modo invitado activado"
                            },
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Tu biblioteca elegante en azul marino y dorado.",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Genero favorito: $favoriteGenre",
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                onOpenSearch(null)
                            }
                        ) {
                            Text("Explorar")
                        }
                        OutlinedButton(
                            onClick = {
                                onOpenSearch(
                                    favoriteGenre.lowercase()
                                )
                            }
                        ) {
                            Text("Buscar por gusto")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Categorias",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                categories.forEach { category ->
                    Card(
                        modifier = Modifier.clickable {
                            onOpenSearch(
                                category.queryValue
                            )
                        },
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme
                                    .primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme
                                        .primaryContainer
                                )
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = category.label
                            )
                            Text(
                                text = category.label
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Recomendados para hoy",
                style = MaterialTheme.typography.titleLarge
            )
        }

        when (val currentState = state) {
            UiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cargando recomendaciones...")
                    }
                }
            }

            is UiState.Error -> {
                item {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is UiState.Success -> {
                items(currentState.data.take(5)) { book ->
                    BookCard(
                        book = book,
                        onClick = {
                            onBookClick(book)
                        },
                        onFavoriteToggle = onFavoriteToggle
                    )
                }
            }
        }
    }
}
