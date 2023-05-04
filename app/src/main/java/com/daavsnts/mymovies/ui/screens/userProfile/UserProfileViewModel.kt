package com.daavsnts.mymovies.ui.screens.userProfile

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daavsnts.mymovies.MyMoviesApplication
import com.daavsnts.mymovies.repository.SettingsRepository
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class UserProfileViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    private val _userName = MutableStateFlow<ScreenUiState<String>>(ScreenUiState.Loading)
    val userName: Flow<ScreenUiState<String>> = _userName
    private val _profilePictureUri = MutableStateFlow<ScreenUiState<String>>(ScreenUiState.Loading)
    val profilePictureUri: Flow<ScreenUiState<String>> = _profilePictureUri

    init {
        setupProfileUiStates()
    }

    private fun setupProfileUiStates() {
        updatePreferencesUiState(_userName, stringPreferencesKey("user_name"), "")
        updatePreferencesUiState(
            _profilePictureUri,
            stringPreferencesKey("profile_picture_uri"),
            ""
        )
    }

    private fun <T> updatePreferencesUiState(
        preferenceUiState: MutableStateFlow<ScreenUiState<T>>,
        key: Preferences.Key<T>,
        defaultValue: T,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceUiState.value = ScreenUiState.Loading
            try {
                val preferenceUiStateDeferred = async {
                    settingsRepository.getPreference(key, defaultValue)
                }
                val preferenceUiStateAwaited = preferenceUiStateDeferred.await()
                withContext(Dispatchers.Main) {
                    preferenceUiStateAwaited.collect {
                        preferenceUiState.value = ScreenUiState.Success(it)
                    }
                }
            } catch (e: IOException) {
                ScreenUiState.Error(e.message)
            }
        }
    }

    private fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.insertPreference(key, value)
        }
    }

    fun setUserName(userName: String) {
        setPreference(stringPreferencesKey("user_name"), userName)
        updatePreferencesUiState(_userName, stringPreferencesKey("user_name"), "")
    }

    fun setProfilePictureUri(profilePictureUri: String) {
        setPreference(stringPreferencesKey("profile_picture_uri"), profilePictureUri)
        updatePreferencesUiState(
            _profilePictureUri,
            stringPreferencesKey("profile_picture_uri"),
            ""
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyMoviesApplication)
                val settingsRepository: SettingsRepository =
                    application.container.settingsRepository
                UserProfileViewModel(settingsRepository)
            }
        }
    }
}