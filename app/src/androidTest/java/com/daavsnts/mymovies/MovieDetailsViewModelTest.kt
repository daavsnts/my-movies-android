package com.daavsnts.mymovies

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.daavsnts.mymovies.data.local.room.UserDao
import com.daavsnts.mymovies.data.local.room.UserDatabase
import com.daavsnts.mymovies.data.network.MoviesApiService
import com.daavsnts.mymovies.data.network.MoviesRetrofitBuilder
import com.daavsnts.mymovies.model.Genre
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.repository.LocalUserRepository
import com.daavsnts.mymovies.repository.MoviesRepository
import com.daavsnts.mymovies.repository.NetworkMoviesRepository
import com.daavsnts.mymovies.repository.UserRepository
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import com.daavsnts.mymovies.ui.screens.movieDetails.MovieDetailsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

class MovieDetailsViewModelTest {
    private val service: MoviesApiService = MoviesRetrofitBuilder.apiService
    private val moviesRepository: MoviesRepository = NetworkMoviesRepository(service)
    private lateinit var userDb: UserDatabase
    private lateinit var userDao: UserDao
    private lateinit var userRepository: UserRepository
    private lateinit var movieDetailsViewModel: MovieDetailsViewModel
    private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    @Before
    fun setupRepositoriesAndViewModel() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        userDb = Room.inMemoryDatabaseBuilder(
            context, UserDatabase::class.java
        ).build()
        userDao = userDb.userDao()
        userRepository = LocalUserRepository(userDao, context.settingsDataStore)
        movieDetailsViewModel = MovieDetailsViewModel(moviesRepository, userRepository)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        userDb.close()
    }

    private val detailedFightClubMovie = Movie(
        550,
        "Fight Club",
        "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \"fight clubs\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.",
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

    @Test
    fun testSetMovieDetails() = runBlocking {
        movieDetailsViewModel.setMovieDetails(detailedFightClubMovie.id)
        delay(1000)
        val movieDetailsUiState = movieDetailsViewModel.movieDetailUiState.first()
        if (movieDetailsUiState is ScreenUiState.Success) {
            assert(movieDetailsUiState.data == detailedFightClubMovie)
        }
    }

    @Test
    fun testAddFavoriteMovie() =
        favoriteSwitcher(movieDetailsViewModel::addFavoriteMovie, true)

    @Test
    fun testRemoveFavoriteMovie() =
        favoriteSwitcher(movieDetailsViewModel::removeFavoriteMovie, false)

    private fun favoriteSwitcher(
        favoriteSwitcherFunction: (Movie) -> Unit,
        favoriteAssertion: Boolean,
    ) = runBlocking {
        movieDetailsViewModel.setMovieDetails(detailedFightClubMovie.id)
        favoriteSwitcherFunction(detailedFightClubMovie)
        delay(1000)
        val movieDetailsUiState = movieDetailsViewModel.movieDetailUiState.first()
        val isMovieFavorite = movieDetailsViewModel.isMovieFavorite.first()

        if (movieDetailsUiState is ScreenUiState.Success)
            assertEquals(isMovieFavorite, favoriteAssertion)
    }

}