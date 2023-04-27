package com.daavsnts.mymovies.repository

import androidx.annotation.WorkerThread
import com.daavsnts.mymovies.data.local.UserDao
import com.daavsnts.mymovies.model.FavoriteMovieId
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val allFavoriteMoviesIds: Flow<List<FavoriteMovieId>>
    suspend fun isMovieFavorite(movieId: Int): Boolean
    suspend fun insertFavoriteMovie(movieId: FavoriteMovieId)
    suspend fun deleteFavoriteMovie(movieId: FavoriteMovieId)
    suspend fun searchFavoriteMovies(searchTerm: String): Flow<List<FavoriteMovieId>>
}

class LocalUserRepository(private val userDao: UserDao) : UserRepository {
    override val allFavoriteMoviesIds: Flow<List<FavoriteMovieId>> = userDao.allFavoriteMoviesIds()

    @WorkerThread
    override suspend fun isMovieFavorite(movieId: Int): Boolean =
        userDao.isMovieFavorite(movieId) > 0

    @WorkerThread
    override suspend fun insertFavoriteMovie(movieId: FavoriteMovieId) =
        userDao.insertFavoriteMovie(movieId)

    @WorkerThread
    override suspend fun deleteFavoriteMovie(movieId: FavoriteMovieId) =
        userDao.deleteFavoriteMovie(movieId)

    @WorkerThread
    override suspend fun searchFavoriteMovies(searchTerm: String): Flow<List<FavoriteMovieId>> =
        userDao.searchFavoriteMovies(searchTerm)
}