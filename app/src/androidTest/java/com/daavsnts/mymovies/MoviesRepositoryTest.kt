package com.daavsnts.mymovies

import com.daavsnts.mymovies.data.network.MoviesApiService
import com.daavsnts.mymovies.data.network.MoviesRetrofitBuilder
import com.daavsnts.mymovies.domain.model.Genre
import com.daavsnts.mymovies.domain.model.Movie
import com.daavsnts.mymovies.data.repository.NetworkMoviesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.reflect.KSuspendFunction0

class MoviesRepositoryTest {
    private val service: MoviesApiService = MoviesRetrofitBuilder.apiService
    private val repository = NetworkMoviesRepository(service)

    @Test
    fun testGetTrendingMovies() = moviesListIsNotEmpty(repository::getTrendingMovies)

    @Test
    fun testGetPopularMovies() = moviesListIsNotEmpty(repository::getPopularMovies)

    @Test
    fun testGetUpcomingMovies() = moviesListIsNotEmpty(repository::getUpcomingMovies)

    private fun moviesListIsNotEmpty(getList: KSuspendFunction0<List<Movie>>) = runBlocking {
        assert(getList().isNotEmpty())
    }

    @Test
    fun testGetMovieDetails() = runBlocking {
        val fightClubMovie = repository.getMovieDetails(550)
        val detailedFightClubMovie = Movie(550,
            "Fight Club",
            overview = "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \"fight clubs\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.",
            releaseDate = "10/15/1999",
            posterPath = "https://image.tmdb.org/t/p/original/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
            voteAverage = 8.433f,
            voteCount = 26288,
            genres = listOf(
                Genre(18, "Drama"),
                Genre(53, "Thriller"),
                Genre(35, "Comedy")
            )
        )
        delay(1000)
        assert(fightClubMovie.title == detailedFightClubMovie.title)
    }

    @Test
    fun testSearchMoviesByTerm() = runBlocking {
        val searchedMoviesList = repository.searchMoviesByTerm("Fight Club")
        val fightClubMovie = Movie(550,
            "Fight Club",
            "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \"fight clubs\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.",
            releaseDate = "10/15/1999",
            posterPath = "https://image.tmdb.org/t/p/original/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
            voteAverage = 8.433f,
            voteCount = 26288,
            genres = null
        )
        delay(1000)
        var contains = false
        searchedMoviesList.forEach {
            if (it.title == fightClubMovie.title) contains = true
        }
        assert(contains)
    }
}