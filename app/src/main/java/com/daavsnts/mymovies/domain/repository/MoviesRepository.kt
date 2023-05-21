package com.daavsnts.mymovies.domain.repository

import com.daavsnts.mymovies.domain.model.Movie

interface MoviesRepository {
    suspend fun getTrendingMovies(): List<Movie>
    suspend fun getPopularMovies(): List<Movie>
    suspend fun getUpcomingMovies(): List<Movie>
    suspend fun getMovieDetails(movieId: Int): Movie
    suspend fun searchMoviesByTerm(searchTerm: String): List<Movie>
}