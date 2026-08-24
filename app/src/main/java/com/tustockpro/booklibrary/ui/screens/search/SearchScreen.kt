package com.tustockpro.booklibrary.ui.screens.search

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ManageSearch
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.domain.model.BookSearchField
import com.tustockpro.booklibrary.ui.components.BookCard
import com.tustockpro.booklibrary.ui.viewmodel.AvailabilityFilter
import com.tustockpro.booklibrary.ui.viewmodel.HomeViewModel
import com.tustockpro.booklibrary.ui.viewmodel.RatingFilter
import com.tustockpro.booklibrary.ui.viewmodel.UiState
import kotlinx.coroutines.launch

private data class SearchCategory(
    val label: String,
    val queryValue: String?,
    val icon: ImageVector
)

private val searchCategories = listOf(
    SearchCategory("Todas", null, Icons.Outlined.Category),
    SearchCategory("Fantasía", "fantasy", Icons.Outlined.AutoStories),
    SearchCategory("Romance", "romance", Icons.Outlined.AutoStories),
    SearchCategory("Tecnología", "technology", Icons.Outlined.ManageSearch),
    SearchCategory("Ciencia", "science", Icons.Outlined.PersonSearch)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onBookClick: (Book) -> Unit,
    onFavoriteToggle: (Book) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()
    val searchField by viewModel.searchField.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val availabilityFilter by viewModel.availabilityFilter.collectAsState()
    val ratingFilter by viewModel.ratingFilter.collectAsState()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Permiso concedido. Ingresa el ISBN manualmente para una búsqueda rápida."
                    )
                }
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Permiso de camara denegado. Puedes escribir el ISBN manualmente."
                    )
                }
            }
        }

    fun handleCameraPermission() {
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Permiso de camara activo. Puedes ingresar el ISBN manualmente para buscar."
                )
            }
        } else {
            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text("Buscar")
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::updateQuery,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Buscar libros") },
                    placeholder = {
                        Text("Titulo, autor, categoria o ISBN")
                    },
                    trailingIcon = {
                        Row {
                            IconButton(
                                onClick = viewModel::search
                            ) {
                                Icon(
                                    imageVector =
                                        Icons.Outlined.ManageSearch,
                                    contentDescription = "Buscar"
                                )
                            }
                            IconButton(
                                onClick = {
                                    handleCameraPermission()
                                }
                            ) {
                                Icon(
                                    imageVector =
                                        Icons.Outlined.QrCodeScanner,
                                    contentDescription =
                                        "Escanear ISBN"
                                )
                            }
                        }
                    }
                )
            }

            item {
                Text(
                    text = "Modo de busqueda",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                FlowRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        BookSearchField.GENERAL to "General",
                        BookSearchField.TITLE to "Titulo",
                        BookSearchField.AUTHOR to "Autor",
                        BookSearchField.CATEGORY to "Categoria"
                    ).forEach { (field, label) ->
                        FilterChip(
                            selected = searchField == field,
                            onClick = {
                                viewModel.selectSearchField(field)
                            },
                            label = {
                                Text(label)
                            }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Categorias visibles",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    searchCategories.forEach { category ->
                        FilterChip(
                            selected =
                                selectedCategory == category.queryValue,
                            onClick = {
                                viewModel.selectCategory(
                                    category.queryValue
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = category.label
                                )
                            },
                            label = {
                                Text(category.label)
                            }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Filtros combinables",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                FlowRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        AvailabilityFilter.ALL to "Disponibilidad: todas",
                        AvailabilityFilter.AVAILABLE to "Solo disponibles",
                        AvailabilityFilter.UNAVAILABLE to "Sin vista previa"
                    ).forEach { (filter, label) ->
                        FilterChip(
                            selected = availabilityFilter == filter,
                            onClick = {
                                viewModel.selectAvailabilityFilter(
                                    filter
                                )
                            },
                            label = {
                                Text(label)
                            }
                        )
                    }

                    listOf(
                        RatingFilter.ALL to "Cualquier valoracion",
                        RatingFilter.THREE_PLUS to "3.0+",
                        RatingFilter.FOUR_PLUS to "4.0+"
                    ).forEach { (filter, label) ->
                        FilterChip(
                            selected = ratingFilter == filter,
                            onClick = {
                                viewModel.selectRatingFilter(
                                    filter
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector =
                                        Icons.Outlined.StarOutline,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(label)
                            }
                        )
                    }
                }
            }

            if (selectedCategory != null) {
                item {
                    Text(
                        text = "Categoria activa: $selectedCategory"
                    )
                }
            }

            when (val currentState = uiState) {
                UiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Buscando en Google Books...")
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
                    if (currentState.data.isEmpty()) {
                        item {
                            Text(
                                "No hay resultados para los filtros actuales."
                            )
                        }
                    } else {
                        items(currentState.data) { book ->
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
    }
}
