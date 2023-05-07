package com.daavsnts.mymovies.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daavsnts.mymovies.model.FavoriteMovieId
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM favorite_movies ORDER BY id DESC")
    fun allFavoriteMoviesIds(): Flow<List<FavoriteMovieId>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMovie(movieId: FavoriteMovieId)

    @Delete
    suspend fun deleteFavoriteMovie(movieId: FavoriteMovieId)

    @Query("SELECT COUNT(*) FROM favorite_movies WHERE id = :movieId")
    suspend fun isMovieFavorite(movieId: Int): Int

    @Query("SELECT * FROM favorite_movies WHERE title LIKE '%' || :searchTerm || '%' ORDER BY id DESC")
    fun searchFavoriteMovies(searchTerm: String): Flow<List<FavoriteMovieId>>
}