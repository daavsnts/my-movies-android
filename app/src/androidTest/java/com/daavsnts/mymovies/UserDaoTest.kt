package com.daavsnts.mymovies

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.daavsnts.mymovies.data.local.room.UserDao
import com.daavsnts.mymovies.data.local.room.UserDatabase
import com.daavsnts.mymovies.model.FavoriteMovieId
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var userDb: UserDatabase
    private lateinit var userDao: UserDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        userDb = Room.inMemoryDatabaseBuilder(
            context, UserDatabase::class.java
        ).build()
        userDao = userDb.userDao()
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
        userDao.insertFavoriteMovie(favoriteMovieId)
        delay(1000)
        assert(userDao.isMovieFavorite(favoriteMovieId.id) > 0)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteFavoriteMovie() = runBlocking {
        val favoriteMovieId =
            FavoriteMovieId(640146, "Ant-Man and the Wasp: Quantumania")
        userDao.insertFavoriteMovie(favoriteMovieId)
        delay(1000)
        assert(userDao.isMovieFavorite(favoriteMovieId.id) > 0)

        userDao.deleteFavoriteMovie(favoriteMovieId)
        delay(1000)
        assert(userDao.isMovieFavorite(favoriteMovieId.id) == 0)
    }

    @Test
    @Throws(Exception::class)
    fun testAllFavoriteMovieIds() = runBlocking {
        val favoriteMovieIdList = listOf(
            FavoriteMovieId(640146, "Ant-Man and the Wasp: Quantumania"),
            FavoriteMovieId(502356, "The Super Mario Bros. Movie"),
            FavoriteMovieId(594767, "Shazam! Fury of the Gods")
        )
        favoriteMovieIdList.forEach { userDao.insertFavoriteMovie(it) }
        delay(1000)

        val allFavoriteMoviesId = userDao.allFavoriteMoviesIds().first()
        assert(allFavoriteMoviesId.containsAll(favoriteMovieIdList))
    }

    @Test
    @Throws(Exception::class)
    fun testSearchFavoriteMovies() = runBlocking {
        val antManFavoriteMovieId = FavoriteMovieId(640146, "Ant-Man and the Wasp: Quantumania")
        userDao.insertFavoriteMovie(antManFavoriteMovieId)
        delay(1000)

        val searchedList = userDao.searchFavoriteMovies("Ant-Man").first()
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
        favoriteMovieIdList.forEach { userDao.insertFavoriteMovie(it) }
        delay(1000)
        val allFavoriteMoviesIdQuantity = userDao.getAllFavoriteMoviesQuantity()
        assertEquals(allFavoriteMoviesIdQuantity, favoriteMovieIdList.size)
    }

}