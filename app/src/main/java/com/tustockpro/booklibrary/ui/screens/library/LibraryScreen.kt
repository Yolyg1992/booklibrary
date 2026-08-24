package com.tustockpro.booklibrary.ui.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tustockpro.booklibrary.domain.model.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    favorites: List<Book>,
    onRead: (Book) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi biblioteca") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (favorites.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Todavía no tienes libros favoritos.",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Guarda libros desde la vista de detalle.",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                favorites.forEach { book ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = book.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = book.authors.joinToString(", ").ifBlank { "Autor desconocido" },
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Button(
                                onClick = { onRead(book) },
                                modifier = Modifier.padding(top = 12.dp)
                            ) {
                                Text("Leer")
                            }
                        }
                    }
                }
            }
        }
    }
}
