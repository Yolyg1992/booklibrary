package com.tustockpro.booklibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tustockpro.booklibrary.domain.model.FontScaleOption
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

            val fontScale by settingsViewModel.fontScale.collectAsState(
                initial = FontScaleOption.NORMAL
            )

            val density = LocalDensity.current

            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = density.density,
                    fontScale = fontScale.scale
                )
            ) {
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
}
