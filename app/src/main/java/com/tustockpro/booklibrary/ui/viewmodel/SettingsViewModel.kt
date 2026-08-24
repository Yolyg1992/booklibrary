package com.tustockpro.booklibrary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tustockpro.booklibrary.domain.model.FontScaleOption
import com.tustockpro.booklibrary.domain.model.UserProfile
import com.tustockpro.booklibrary.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: UserPreferences
) : ViewModel() {

    val darkTheme: Flow<Boolean> =
        preferences.darkTheme

    val userName: Flow<String> =
        preferences.userName

    val favoriteGenre: Flow<String> =
        preferences.favoriteGenre

    val fontScale: Flow<FontScaleOption> =
        preferences.fontScale

    val sessionActive: Flow<Boolean> =
        preferences.sessionActive

    val profile: Flow<UserProfile> =
        combine(userName, favoriteGenre) { name, genre ->
            UserProfile(
                name = name,
                favoriteGenre = genre
            )
        }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setDarkTheme(enabled)
        }
    }

    fun setUserName(value: String) {
        viewModelScope.launch {
            preferences.setUserName(value)
        }
    }

    fun setFavoriteGenre(value: String) {
        viewModelScope.launch {
            preferences.setFavoriteGenre(value)
        }
    }

    fun setFontScale(value: FontScaleOption) {
        viewModelScope.launch {
            preferences.setFontScale(value)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            preferences.signOut()
        }
    }

    class Factory(
        private val preferences: UserPreferences
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            return SettingsViewModel(
                preferences = preferences
            ) as T
        }
    }
}
