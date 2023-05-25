package com.daavsnts.mymovies.ui.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MoviesScreenNavigationViewModel @Inject constructor(startDestination: String) : ViewModel() {
    private val _currentDestination = MutableStateFlow(startDestination)
    val currentDestination: Flow<String> = _currentDestination
    private val _currentMovieId = MutableStateFlow(0)
    val currentMovieId: Flow<Int> = _currentMovieId

    fun setDestination(destination: String) { _currentDestination.value = destination }

    fun setDetailsMovieId(movieId: Int) { _currentMovieId.value = movieId }
}

@HiltViewModel
class FavoriteScreenNavigationViewModel @Inject constructor(startDestination: String) : ViewModel() {
    private val _currentDestination = MutableStateFlow(startDestination)
    val currentDestination: Flow<String> = _currentDestination
    private val _currentMovieId = MutableStateFlow(0)
    val currentMovieId: Flow<Int> = _currentMovieId

    fun setDestination(destination: String) { _currentDestination.value = destination }

    fun setDetailsMovieId(movieId: Int) { _currentMovieId.value = movieId }
}