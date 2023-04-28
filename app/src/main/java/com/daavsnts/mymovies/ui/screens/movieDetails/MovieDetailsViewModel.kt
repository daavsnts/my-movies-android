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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import kotlin.properties.Delegates

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
        viewModelScope.launch {
            _movieDetailUiState.value = ScreenUiState.Loading
            lateinit var moviesDetailsUiStateResponse: ScreenUiState<Movie>

            withContext(Dispatchers.IO) {
                moviesDetailsUiStateResponse = try {
                    ScreenUiState.Success(moviesRepository.getMovieDetails(movieId))
                } catch (e: IOException) {
                    ScreenUiState.Error
                } catch (e: HttpException) {
                    ScreenUiState.Error
                }
            }

            _movieDetailUiState.value = moviesDetailsUiStateResponse
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
        viewModelScope.launch {
            var isMovieFavoriteResponse by Delegates.notNull<Boolean>()
            withContext(Dispatchers.IO) {
                isMovieFavoriteResponse = userRepository.isMovieFavorite(movieId)
            }
            _isMovieFavorite.value = isMovieFavoriteResponse
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