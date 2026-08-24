package com.tustockpro.booklibrary.ui.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.tustockpro.booklibrary.ui.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel,
    onBookClick: (Book) -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Favoritos")
                }
            )
        }
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Todavia no guardas libros en Room.",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                items(favorites) { book ->
                    BookCard(
                        book = book,
                        onClick = {
                            onBookClick(book)
                        },
                        onFavoriteToggle = viewModel::removeFavorite
                    )
                }
            }
        }
    }
}
