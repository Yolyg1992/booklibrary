package com.tustockpro.booklibrary.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tustockpro.booklibrary.domain.model.FontScaleOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "booklibrary_preferences"
)

class UserPreferences(
    private val context: Context
) {

    companion object {
        private val DARK_THEME =
            booleanPreferencesKey("dark_theme")
        private val USER_NAME =
            stringPreferencesKey("user_name")
        private val FAVORITE_GENRE =
            stringPreferencesKey("favorite_genre")
        private val FONT_SCALE =
            stringPreferencesKey("font_scale")
        private val SESSION_ACTIVE =
            booleanPreferencesKey("session_active")
    }

    val darkTheme: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[DARK_THEME] ?: false
        }

    val userName: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[USER_NAME] ?: "Lector"
        }

    val favoriteGenre: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[FAVORITE_GENRE] ?: "Fantasía"
        }

    val fontScale: Flow<FontScaleOption> =
        context.dataStore.data.map { preferences ->
            FontScaleOption.fromStorage(
                preferences[FONT_SCALE]
            )
        }

    val sessionActive: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[SESSION_ACTIVE] ?: true
        }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME] = enabled
        }
    }

    suspend fun setUserName(value: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = value.ifBlank { "Lector" }
            preferences[SESSION_ACTIVE] = true
        }
    }

    suspend fun setFavoriteGenre(value: String) {
        context.dataStore.edit { preferences ->
            preferences[FAVORITE_GENRE] = value.ifBlank { "Fantasía" }
        }
    }

    suspend fun setFontScale(value: FontScaleOption) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SCALE] = value.name
        }
    }

    suspend fun signOut() {
        context.dataStore.edit { preferences ->
            preferences[SESSION_ACTIVE] = false
            preferences[USER_NAME] = "Invitado"
        }
    }
}
