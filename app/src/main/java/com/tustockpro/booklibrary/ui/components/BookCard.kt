package com.tustockpro.booklibrary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tustockpro.booklibrary.domain.model.Book

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    onFavoriteToggle: ((Book) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (book.coverUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .size(
                            width = 80.dp,
                            height = 110.dp
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = androidx.compose.ui.Alignment.Center
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
                    modifier = Modifier.size(
                        width = 80.dp,
                        height = 110.dp
                    ),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    if (onFavoriteToggle != null) {
                        IconButton(
                            onClick = {
                                onFavoriteToggle(book)
                            }
                        ) {
                            Icon(
                                imageVector =
                                    if (book.isFavorite) {
                                        Icons.Filled.Favorite
                                    } else {
                                        Icons.Outlined.FavoriteBorder
                                    },
                                contentDescription = "Favorito",
                                tint =
                                    if (book.isFavorite) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                            )
                        }
                    }
                }

                Text(
                    text = book.authors.joinToString(", ")
                        .ifBlank { "Autor por confirmar" },
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                if (book.categories.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        book.categories.take(2).forEach { category ->
                            Text(
                                text = category,
                                style =
                                    MaterialTheme.typography.labelMedium,
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(50)
                                    )
                                    .background(
                                        MaterialTheme.colorScheme
                                            .primaryContainer
                                    )
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    )
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )
                }

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text =
                            if (book.isAvailable) {
                                "Disponible"
                            } else {
                                "Sin vista previa"
                            },
                        style = MaterialTheme.typography.bodySmall
                    )

                    book.averageRating?.let { rating ->
                        Text(
                            text = "★ ${"%.1f".format(rating)}",
                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }
                }

                book.addedAt?.let { addedAt ->
                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )
                    Text(
                        text = "Guardado el ${java.text.DateFormat.getDateInstance().format(addedAt)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
