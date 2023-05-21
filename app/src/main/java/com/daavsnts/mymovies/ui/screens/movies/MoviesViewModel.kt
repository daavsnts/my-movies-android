package com.daavsnts.mymovies.ui.screens.movies

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import com.daavsnts.mymovies.repository.MoviesRepository
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daavsnts.mymovies.MyMoviesApplication
import com.daavsnts.mymovies.R
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.reflect.KProperty0

class PairOfListMoviesState(
    @StringRes val title: Int,
    val list: Flow<ScreenUiState<List<Movie>>>
)

class PairOfStatesWithRepositoryFunctions(
    val stateList: MutableStateFlow<ScreenUiState<List<Movie>>>,
    val apiFunction: suspend () -> List<Movie>
)

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
    ) : ViewModel() {
    private val _trendingMoviesUiState =
        MutableStateFlow<ScreenUiState<List<Movie>>>(ScreenUiState.Loading)
    private val trendingMoviesUiState: Flow<ScreenUiState<List<Movie>>> = _trendingMoviesUiState

    private val _popularMoviesUiState =
        MutableStateFlow<ScreenUiState<List<Movie>>>(ScreenUiState.Loading)
    private val popularMoviesUiState: Flow<ScreenUiState<List<Movie>>> = _popularMoviesUiState

    private val _upcomingMoviesUiState =
        MutableStateFlow<ScreenUiState<List<Movie>>>(ScreenUiState.Loading)
    private val upcomingMoviesUiState: Flow<ScreenUiState<List<Movie>>> = _upcomingMoviesUiState

    private val _searchedMoviesUiState =
        MutableStateFlow<ScreenUiState<List<Movie>>>(ScreenUiState.Loading)
    val searchedMoviesUiState: Flow<ScreenUiState<List<Movie>>> = _searchedMoviesUiState

    private val moviesUiStateWithApiFunctions = listOf(
        PairOfStatesWithRepositoryFunctions(
            _trendingMoviesUiState,
            moviesRepository::getTrendingMovies
        ),
        PairOfStatesWithRepositoryFunctions(
            _popularMoviesUiState,
            moviesRepository::getPopularMovies
        ),
        PairOfStatesWithRepositoryFunctions(
            _upcomingMoviesUiState,
            moviesRepository::getUpcomingMovies
        )
    )

    val moviesUiStatesList = listOf(
        PairOfListMoviesState(R.string.ms_trending_title, trendingMoviesUiState),
        PairOfListMoviesState(R.string.ms_popular_title, popularMoviesUiState),
        PairOfListMoviesState(R.string.ms_upcoming_title, upcomingMoviesUiState)
    )

    init {
        setupMoviesUiStates()
    }

    private fun setupMoviesUiStates() {
        moviesUiStateWithApiFunctions.forEach {
            getMovies(it::stateList, it.apiFunction)
        }
    }

    private fun getMovies(
        moviesUiState: KProperty0<MutableStateFlow<ScreenUiState<List<Movie>>>>,
        getMoviesFunction: suspend () -> List<Movie>
    ) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            moviesUiState.get().value = ScreenUiState.Error(throwable.message)
        }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            moviesUiState.get().value = ScreenUiState.Loading
            val moviesUiStateListDeferred = async { getMoviesFunction() }
            val moviesUiStateList = moviesUiStateListDeferred.await()
            withContext(Dispatchers.Main) {
                moviesUiState.get().value = ScreenUiState.Success(moviesUiStateList)
            }

        }
    }

    fun setSearchedMoviesList(searchTerm: String) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _searchedMoviesUiState.value = ScreenUiState.Error(throwable.message)
        }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _searchedMoviesUiState.value = ScreenUiState.Loading
            val searchedMoviesUiStateDeferred =
                async { moviesRepository.searchMoviesByTerm(searchTerm) }
            val searchedMoviesUiState = searchedMoviesUiStateDeferred.await()
            withContext(Dispatchers.Main) {
                _searchedMoviesUiState.value = ScreenUiState.Success(searchedMoviesUiState)
            }
        }
    }
}