package com.daavsnts.mymovies.ui.screens.favoriteMovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daavsnts.mymovies.MyMoviesApplication
import com.daavsnts.mymovies.repository.UserRepository
import com.daavsnts.mymovies.repository.MoviesRepository
import com.daavsnts.mymovies.model.FavoriteMovieId
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class FavoriteMoviesViewModel(
    private val moviesRepository: MoviesRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _favoriteMoviesUiState =
        MutableStateFlow<ScreenUiState<List<Movie>>>(ScreenUiState.Loading)
    val favoriteMoviesUiState: Flow<ScreenUiState<List<Movie>>> = _favoriteMoviesUiState

    private val _searchedMoviesUiState =
        MutableStateFlow<ScreenUiState<List<Movie>>>(ScreenUiState.Loading)
    val searchedMoviesUiState: Flow<ScreenUiState<List<Movie>>> = _searchedMoviesUiState

    init {
        setupFavoriteMovies()
    }

    private fun setupFavoriteMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            _favoriteMoviesUiState.value = ScreenUiState.Loading
            try {
                val favoritesIdListDeferred = async { userRepository.allFavoriteMoviesIds }
                val favoritesIdList = favoritesIdListDeferred.await()
                withContext(Dispatchers.Main) {
                    favoritesIdList.collect {
                        _favoriteMoviesUiState.value =
                            ScreenUiState.Success(getFavoriteMoviesListByIds(it))
                    }
                }
            } catch (e: HttpException) {
                ScreenUiState.Error(e.message)
            }
        }
    }

    fun setSearchedMoviesList(searchTerm: String) {
        viewModelScope.launch {
            _searchedMoviesUiState.value = ScreenUiState.Loading
            try {
                val searchFavoritesIdListDeferred =
                    async { userRepository.searchFavoriteMovies(searchTerm) }
                val searchFavoritesIdList = searchFavoritesIdListDeferred.await()
                withContext(Dispatchers.Main) {
                    searchFavoritesIdList.collect {
                        _searchedMoviesUiState.value =
                            ScreenUiState.Success(getFavoriteMoviesListByIds(it))
                    }
                }
            } catch (e: HttpException) {
                ScreenUiState.Error(e.message)
            }
        }
    }

    private suspend fun getFavoriteMoviesListByIds(
        favoriteMoviesIdList: List<FavoriteMovieId>
    ): MutableList<Movie> {
        val favoriteMoviesList = mutableListOf<Movie>()
        favoriteMoviesIdList.forEach {
            delay(300)
            favoriteMoviesList.add(moviesRepository.getMovieDetails(it.id))
        }
        return favoriteMoviesList
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyMoviesApplication)
                val moviesRepository: MoviesRepository = application.container.moviesRepository
                val userRepository: UserRepository = application.container.userRepository
                FavoriteMoviesViewModel(moviesRepository, userRepository)
            }
        }
    }
}