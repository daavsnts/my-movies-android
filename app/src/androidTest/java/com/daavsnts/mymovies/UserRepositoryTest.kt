package com.daavsnts.mymovies

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.daavsnts.mymovies.data.local.room.UserDao
import com.daavsnts.mymovies.data.local.room.UserDatabase
import com.daavsnts.mymovies.domain.model.FavoriteMovieId
import com.daavsnts.mymovies.data.repository.LocalUserRepository
import com.daavsnts.mymovies.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

class UserRepositoryTest {
    private lateinit var userDb: UserDatabase
    private lateinit var userDao: UserDao
    private lateinit var userRepository: UserRepository
    private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        userDb = Room.inMemoryDatabaseBuilder(
            context, UserDatabase::class.java
        ).build()
        userDao = userDb.userDao()
        userRepository = LocalUserRepository(userDao, context.settingsDataStore)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        userDb.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertFavoriteMovie() = runBlocking {
        val favoriteMovieId = FavoriteMovieId(640146, "Ant-Man and the Wasp: Quantumania")

        userRepository.insertFavoriteMovie(favoriteMovieId)
        delay(1000)
        assert(userRepository.isMovieFavorite(favoriteMovieId.id))
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteFavoriteMovie() = runBlocking {
        val favoriteMovieId =
            FavoriteMovieId(640146, "Ant-Man and the Wasp: Quantumania")

        userRepository.insertFavoriteMovie(favoriteMovieId)
        delay(1000)
        assert(userRepository.isMovieFavorite(favoriteMovieId.id))

        userRepository.deleteFavoriteMovie(favoriteMovieId)
        delay(1000)
        assert(!userRepository.isMovieFavorite(favoriteMovieId.id))
    }

    @Test
    @Throws(Exception::class)
    fun testAllFavoriteMovieIds() = runBlocking {
        val favoriteMovieIdList = listOf(
            FavoriteMovieId(640146, "Ant-Man and the Wasp: Quantumania"),
            FavoriteMovieId(502356, "The Super Mario Bros. Movie"),
            FavoriteMovieId(594767, "Shazam! Fury of the Gods")
        )
        favoriteMovieIdList.forEach { userRepository.insertFavoriteMovie(it) }
        delay(1000)

        val allFavoriteMoviesId = userRepository.allFavoriteMoviesIds.first()
        assert(allFavoriteMoviesId.containsAll(favoriteMovieIdList))
    }

    @Test
    @Throws(Exception::class)
    fun testSearchFavoriteMovies() = runBlocking {
        val antManFavoriteMovieId = FavoriteMovieId(640146, "Ant-Man and the Wasp: Quantumania")
        userRepository.insertFavoriteMovie(antManFavoriteMovieId)
        delay(1000)

        val searchedList = userRepository.searchFavoriteMovies("Ant-Man").first()
        assert(searchedList.contains(antManFavoriteMovieId))
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllFavoriteMoviesQuantity() = runBlocking {
        val favoriteMovieIdList = listOf(
            FavoriteMovieId(640146, "Ant-Man and the Wasp: Quantumania"),
            FavoriteMovieId(502356, "The Super Mario Bros. Movie"),
            FavoriteMovieId(594767, "Shazam! Fury of the Gods")
        )
        favoriteMovieIdList.forEach { userRepository.insertFavoriteMovie(it) }
        delay(1000)

        val allFavoriteMoviesIdQuantity = userRepository.getAllFavoriteMoviesQuantity().first()
        assertEquals(allFavoriteMoviesIdQuantity, favoriteMovieIdList.size)
    }
}