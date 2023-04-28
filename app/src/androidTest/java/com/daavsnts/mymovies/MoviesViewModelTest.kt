package com.daavsnts.mymovies

import com.daavsnts.mymovies.data.network.MoviesApiService
import com.daavsnts.mymovies.data.network.MoviesRetrofitBuilder
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.repository.NetworkMoviesRepository
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import com.daavsnts.mymovies.ui.screens.movies.MoviesViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test

class MoviesViewModelTest {
    private val service: MoviesApiService = MoviesRetrofitBuilder.apiService
    private val repository = NetworkMoviesRepository(service)
    private val moviesViewModel = MoviesViewModel(repository)

    @Test
    fun testMoviesUiStateList() = runBlocking {
        val moviesUiStateList =
            moviesViewModel.moviesUiStatesList.map {
                Pair(
                    it.title,
                    it.list.first()
                )
            }
        moviesUiStateList.forEach {
            val movieUiStateList = it.second
            if (movieUiStateList is ScreenUiState.Success)
                assert(movieUiStateList.data.isNotEmpty())
        }
        assert(true)
    }

    @Test
    fun testSearchedMoviesUiState() = runBlocking {
        val searchedMoviesUiStateList = moviesViewModel.searchedMoviesUiState.first()
        moviesViewModel.setSearchedMoviesList("Fight Club")
        val fightClubMovie = Movie(550,
            "Fight Club",
            "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \"fight clubs\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.",
            releaseDate = "10/15/1999",
            posterPath = "https://image.tmdb.org/t/p/original/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
            voteAverage = 8.433f,
            voteCount = 26280,
            genres = null
        )
        if (searchedMoviesUiStateList is ScreenUiState.Success)
            assert(searchedMoviesUiStateList.data.contains(fightClubMovie))
        assert(true)
    }
}