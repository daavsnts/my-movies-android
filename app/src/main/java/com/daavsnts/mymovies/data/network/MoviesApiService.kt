package com.daavsnts.mymovies.data.network

import com.daavsnts.mymovies.BuildConfig
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.model.MovieList
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApiService {
    @GET("trending/all/week")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Response<MovieList>

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Response<MovieList>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("language") language: String = "en_US",
        @Query("page") page: Int = 1,
    ): Response<MovieList>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("language") language: String = "en_US",
    ): Response<Movie>

    @GET("search/movie")
    suspend fun searchMoviesByTerm(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("query") searchTerm: String,
        @Query("language") language: String = "en_US"
    ): Response<MovieList>
}

object MoviesRetrofitBuilder {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: MoviesApiService = getRetrofit().create(MoviesApiService::class.java)
}