package com.daavsnts.mymovies.ui.screens.movieDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daavsnts.mymovies.MyMoviesApplication
import com.daavsnts.mymovies.repository.UserRepository
import com.daavsnts.mymovies.repository.MoviesRepository
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.model.FavoriteMovieId
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailsViewModel(
    private val moviesRepository: MoviesRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _movieDetailUiState =
        MutableStateFlow<ScreenUiState<Movie>>(ScreenUiState.Loading)
    val movieDetailUiState: Flow<ScreenUiState<Movie>> = _movieDetailUiState
    private val _isMovieFavorite = MutableStateFlow(false)
    val isMovieFavorite: Flow<Boolean> = _isMovieFavorite

    fun setMovieDetails(movieId: Int) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _movieDetailUiState.value = ScreenUiState.Error(throwable.message)
        }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _movieDetailUiState.value = ScreenUiState.Loading
            val moviesDetailsUiStateDeferred =
                async { moviesRepository.getMovieDetails(movieId) }
            val moviesDetailsUiState = moviesDetailsUiStateDeferred.await()
            withContext(Dispatchers.Main) {
                _movieDetailUiState.value = ScreenUiState.Success(moviesDetailsUiState)
            }
        }
    }

    fun addFavoriteMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.insertFavoriteMovie(FavoriteMovieId(movie.id, movie.title))
            refreshIsMovieFavorite(movie.id)
        }
    }

    fun removeFavoriteMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteFavoriteMovie(FavoriteMovieId(movie.id, movie.title))
            refreshIsMovieFavorite(movie.id)
        }
    }

    fun refreshIsMovieFavorite(movieId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            val isMovieFavoriteDeferred = async { userRepository.isMovieFavorite(movieId) }
            val isMovieFavorite = isMovieFavoriteDeferred.await()
            withContext(Dispatchers.Main) {
                _isMovieFavorite.value = isMovieFavorite
            }
        }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyMoviesApplication)
                val moviesRepository: MoviesRepository = application.container.moviesRepository
                val userRepository: UserRepository = application.container.userRepository
                MovieDetailsViewModel(moviesRepository, userRepository)
            }
        }
    }
}