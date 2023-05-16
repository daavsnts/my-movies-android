package com.daavsnts.mymovies.ui.screens.userProfile

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daavsnts.mymovies.MyMoviesApplication
import com.daavsnts.mymovies.data.local.internaldir.copyFileToInternalDir
import com.daavsnts.mymovies.data.local.internaldir.getFileUriFromInternalDir
import com.daavsnts.mymovies.repository.UserRepository
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class UserProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _userName = MutableStateFlow<ScreenUiState<String>>(ScreenUiState.Loading)
    val userName: Flow<ScreenUiState<String>> = _userName
    private val _profilePictureUri = MutableStateFlow<ScreenUiState<String>>(ScreenUiState.Loading)
    val profilePictureUri: Flow<ScreenUiState<String>> = _profilePictureUri
    private val _profileBackgroundUri =
        MutableStateFlow<ScreenUiState<String>>(ScreenUiState.Loading)
    val profileBackgroundUri: Flow<ScreenUiState<String>> = _profileBackgroundUri
    private val _userFavoriteMoviesQuantity =
        MutableStateFlow<ScreenUiState<String>>(ScreenUiState.Loading)
    val userFavoriteMoviesQuantity: Flow<ScreenUiState<String>> = _userFavoriteMoviesQuantity

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
        updatePreferencesUiState(
            _profileBackgroundUri,
            stringPreferencesKey("profile_background_uri"),
            ""
        )
        updateUserFavoriteMoviesQuantity(_userFavoriteMoviesQuantity)
    }

    private fun updateUserFavoriteMoviesQuantity(
        userFavoriteMoviesQuantityUiState: MutableStateFlow<ScreenUiState<String>>
    ) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            userFavoriteMoviesQuantityUiState.value = ScreenUiState.Error(throwable.message)
        }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val userFavoriteMoviesQuantityUiStateDeferred = async {
                userRepository.getAllFavoriteMoviesQuantity()
            }
            val userFavoriteMoviesQuantityUiStateAwaited =
                userFavoriteMoviesQuantityUiStateDeferred.await()
            withContext(Dispatchers.Main) {
                userFavoriteMoviesQuantityUiStateAwaited.collect {
                    userFavoriteMoviesQuantityUiState.value =
                        ScreenUiState.Success(it.toString())
                }
            }
        }
    }

    private fun <T> updatePreferencesUiState(
        preferenceUiState: MutableStateFlow<ScreenUiState<T>>,
        key: Preferences.Key<T>,
        defaultValue: T,
    ) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            preferenceUiState.value = ScreenUiState.Error(throwable.message)
        }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            preferenceUiState.value = ScreenUiState.Loading
            val preferenceUiStateDeferred = async {
                userRepository.getPreference(key, defaultValue)
            }
            val preferenceUiStateAwaited = preferenceUiStateDeferred.await()
            withContext(Dispatchers.Main) {
                preferenceUiStateAwaited.collect {
                    preferenceUiState.value = ScreenUiState.Success(it)
                }
            }
        }
    }

    private fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.insertPreference(key, value)
        }
    }

    fun setUserName(userName: String) {
        setPreference(stringPreferencesKey("user_name"), userName)
        updatePreferencesUiState(_userName, stringPreferencesKey("user_name"), "")
    }

    fun setProfilePictureUri(
        context: Context,
        profilePictureUri: Uri,
        key: String = "profile_picture_uri"
    ) = setPictureUri(context, profilePictureUri, key, _profilePictureUri)

    fun setBackgroundPictureUri(
        context: Context,
        profilePictureUri: Uri,
        key: String = "profile_background_uri"
    ) = setPictureUri(context, profilePictureUri, key, _profileBackgroundUri)

    private fun copyImageToInternalStorage(
        context: Context,
        profilePictureUri: Uri,
        key: String
    ): Uri {
        copyFileToInternalDir(context, profilePictureUri, key)
        return getFileUriFromInternalDir(context, key)
    }

    private fun setPictureUri(
        context: Context,
        pictureUri: Uri,
        key: String,
        pictureUriState: MutableStateFlow<ScreenUiState<String>>
    ) {
        val pictureUriFromInternalDir =
            copyImageToInternalStorage(context, pictureUri, key)
        setPreference(
            stringPreferencesKey(key),
            pictureUriFromInternalDir.toString()
        )
        updatePreferencesUiState(pictureUriState, stringPreferencesKey(key), "")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyMoviesApplication)
                val userRepository: UserRepository =
                    application.container.userRepository
                UserProfileViewModel(userRepository)
            }
        }
    }
}