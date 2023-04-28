package com.daavsnts.mymovies.ui.screens.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import com.daavsnts.mymovies.repository.MoviesRepository
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daavsnts.mymovies.MyMoviesApplication
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import kotlin.reflect.KProperty0

class PairOfListMoviesState(
    val title: String,
    val list: Flow<ScreenUiState<List<Movie>>>
)

class PairOfStatesWithRepositoryFunctions(
    val stateList: MutableStateFlow<ScreenUiState<List<Movie>>>,
    val apiFunction:  suspend () -> List<Movie>)

class MoviesViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {
    private val _trendingMoviesUiState = MutableStateFlow<ScreenUiState<List<Movie>>>(ScreenUiState.Loading)
    val trendingMoviesUiState: Flow<ScreenUiState<List<Movie>>> = _trendingMoviesUiState

    private val _popularMoviesUiState = MutableStateFlow<ScreenUiState<List<Movie>>>(ScreenUiState.Loading)
    val popularMoviesUiState: Flow<ScreenUiState<List<Movie>>> = _popularMoviesUiState

    private val _upcomingMoviesUiState = MutableStateFlow<ScreenUiState<List<Movie>>>(ScreenUiState.Loading)
    val upcomingMoviesUiState: Flow<ScreenUiState<List<Movie>>> = _upcomingMoviesUiState

    private val _searchedMoviesUiState = MutableStateFlow<ScreenUiState<List<Movie>>>(ScreenUiState.Loading)
    val searchedMoviesUiState: Flow<ScreenUiState<List<Movie>>> = _searchedMoviesUiState

    private val moviesUiStateWithApiFunctions = listOf(
        PairOfStatesWithRepositoryFunctions(_trendingMoviesUiState, moviesRepository::getTrendingMovies),
        PairOfStatesWithRepositoryFunctions(_popularMoviesUiState, moviesRepository::getPopularMovies),
        PairOfStatesWithRepositoryFunctions(_upcomingMoviesUiState, moviesRepository::getUpcomingMovies)
    )

    val moviesUiStatesList = listOf(
        PairOfListMoviesState("Trending Movies", trendingMoviesUiState),
        PairOfListMoviesState("Popular Movies", popularMoviesUiState),
        PairOfListMoviesState("Upcoming Movies", upcomingMoviesUiState)
    )

    init { setupMoviesUiStates() }

    private fun setupMoviesUiStates() {
        moviesUiStateWithApiFunctions.forEach {
            getMovies(it::stateList, it.apiFunction)
        }
    }

    private fun getMovies(
        moviesUiState: KProperty0<MutableStateFlow<ScreenUiState<List<Movie>>>>,
        getMoviesFunction: suspend () -> List<Movie>
    ) {
        viewModelScope.launch {
            moviesUiState.get().value = ScreenUiState.Loading
            moviesUiState.get().value = try {
                ScreenUiState.Success(getMoviesFunction())
            } catch (e: IOException) {
                ScreenUiState.Error
            } catch (e: HttpException) {
                ScreenUiState.Error
            }
        }
    }

    fun setSearchedMoviesList(searchTerm: String) {
        viewModelScope.launch {
            _searchedMoviesUiState.value = ScreenUiState.Loading
            _searchedMoviesUiState.value = try {
                ScreenUiState.Success(moviesRepository.searchMoviesByTerm(searchTerm))
            } catch (e: IOException) {
                ScreenUiState.Error
            } catch (e: HttpException) {
                ScreenUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MyMoviesApplication)
                val moviesRepository: MoviesRepository = application.container.moviesRepository
                MoviesViewModel(moviesRepository)
            }
        }
    }
}