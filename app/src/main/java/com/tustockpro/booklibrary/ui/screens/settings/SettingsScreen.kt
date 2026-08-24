package com.tustockpro.booklibrary.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import com.tustockpro.booklibrary.domain.model.FontScaleOption
import com.tustockpro.booklibrary.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel
) {
    val darkTheme by viewModel.darkTheme.collectAsState(
        initial = false
    )
    val profile by viewModel.profile.collectAsState(
        initial = null
    )
    val fontScale by viewModel.fontScale.collectAsState(
        initial = FontScaleOption.NORMAL
    )
    val sessionActive by viewModel.sessionActive.collectAsState(
        initial = true
    )

    val genres = remember {
        listOf(
            "Fantasía",
            "Romance",
            "Tecnología",
            "Ciencia",
            "Historia",
            "Misterio"
        )
    }

    var userInput by remember(profile?.name) {
        mutableStateOf(profile?.name ?: "Lector")
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Ajustes")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Perfil",
                            style = MaterialTheme.typography.titleLarge
                        )
                        OutlinedTextField(
                            value = userInput,
                            onValueChange = {
                                userInput = it
                                viewModel.setUserName(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("Nombre visible")
                            }
                        )
                        Text(
                            text =
                                if (sessionActive) {
                                    "Sesion activa"
                                } else {
                                    "Sesion cerrada"
                                }
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Modo oscuro",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Text("Aplicar tema nocturno")
                            Switch(
                                checked = darkTheme,
                                onCheckedChange =
                                    viewModel::setDarkTheme
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Tamano de fuente",
                            style = MaterialTheme.typography.titleMedium
                        )
                        FlowRow(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            FontScaleOption.entries.forEach { option ->
                                FilterChip(
                                    selected = fontScale == option,
                                    onClick = {
                                        viewModel.setFontScale(option)
                                    },
                                    label = {
                                        Text(option.label)
                                    }
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
                        defaultElevation = 10.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Genero favorito",
                            style = MaterialTheme.typography.titleMedium
                        )
                        FlowRow(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            genres.forEach { genre ->
                                FilterChip(
                                    selected =
                                        profile?.favoriteGenre == genre,
                                    onClick = {
                                        viewModel.setFavoriteGenre(
                                            genre
                                        )
                                    },
                                    label = {
                                        Text(genre)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = viewModel::signOut,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesion")
                }
            }
        }
    }
}
