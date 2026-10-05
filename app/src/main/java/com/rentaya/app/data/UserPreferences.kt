package com.rentaya.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rentaya_prefs")

class UserPreferences(private val context: Context) {
    
    private object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val FAVORITE_IDS = stringSetPreferencesKey("favorite_ids")
    }
    
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        }
    
    val userEmail: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_EMAIL] ?: ""
        }
    
    val userName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_NAME] ?: ""
        }
    
    val darkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DARK_MODE] ?: false
        }
    
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        }

    val favoriteIds: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_IDS] ?: emptySet()
        }

    suspend fun login(email: String, name: String, phone: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.USER_PHONE] = phone
        }
    }
    
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
            preferences[PreferencesKeys.USER_EMAIL] = ""
            preferences[PreferencesKeys.USER_NAME] = ""
            preferences[PreferencesKeys.USER_PHONE] = ""
        }
    }
    
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }
    
    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setFavoriteIds(ids: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FAVORITE_IDS] = ids
        }
    }
}
