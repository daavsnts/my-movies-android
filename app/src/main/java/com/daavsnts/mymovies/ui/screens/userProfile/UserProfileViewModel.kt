package com.daavsnts.mymovies.ui.screens.userProfile

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
import kotlinx.coroutines.flow.collect
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
        updatePreferenceUiState(_userName, "user_name")
        updatePreferenceUiState(_profilePictureUri, "profile_picture_uri")
    }

    private fun updatePreferenceUiState(
        preferenceUiState: MutableStateFlow<ScreenUiState<String>>,
        key: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceUiState.value = ScreenUiState.Loading
            try {
                val preferenceUiStateStringDeferred = async {
                    settingsRepository.getPreference(stringPreferencesKey(key), "")
                }
                val preferenceUiStateString = preferenceUiStateStringDeferred.await()
                withContext(Dispatchers.Main) {
                    preferenceUiStateString.collect {
                        preferenceUiState.value = ScreenUiState.Success(it)
                    }
                }
            } catch (e: IOException) {
                ScreenUiState.Error(e.message)
            }
        }
    }

    fun setStringPreference(key: String, preference: String) =
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.insertPreference(stringPreferencesKey(key), preference)
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