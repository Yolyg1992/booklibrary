package com.tustockpro.booklibrary.ui.screens.reading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tustockpro.booklibrary.domain.model.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(
    book: Book,
    onBack: () -> Unit
) {
    var fontScale by remember { mutableStateOf(1f) }
    val excerpt = book.description ?: "Este libro ofrece una lectura envolvente con ideas inspiradoras, personajes memorables y una narrativa que invita a seguir explorando cada capítulo."

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book.title) },
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
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { if (fontScale > 0.8f) fontScale -= 0.2f }) { Text("A-") }
                    Button(onClick = { if (fontScale < 1.8f) fontScale += 0.2f }) { Text("A+") }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = excerpt,
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = (20 * fontScale).sp
                    )
                )
            }
        }
    }
}
