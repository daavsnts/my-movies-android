package com.daavsnts.mymovies

import com.daavsnts.mymovies.data.network.MoviesApiService
import com.daavsnts.mymovies.data.network.MoviesRetrofitBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Test
import retrofit2.Response

class MoviesApiServiceTest {
    private val service: MoviesApiService = MoviesRetrofitBuilder.apiService

    @Test
    fun testGetTrendingMovies() =
        assert(checkApiResponse { service.getTrendingMovies() })

    @Test
    fun getPopularMovies() =
        assert(checkApiResponse { service.getPopularMovies() })

    @Test
    fun getUpcomingMovies() =
        assert(checkApiResponse { service.getUpcomingMovies() })

    @Test
    fun getMovieDetails() =
        assert(checkApiResponse { service.getMovieDetails(500) })

    @Test
    fun searchMoviesByTerm() =
        assert(checkApiResponse { service.searchMoviesByTerm(searchTerm = "Avatar") })

    private fun <T> checkApiResponse(
        serviceFunction: suspend () -> Response<T>
    ): Boolean {
        var success = false
        runBlocking {
            val response = serviceFunction()
            val errorBody = response.errorBody()
            assert(errorBody == null)

            val responseWrapper = response.body()
            assert(responseWrapper != null)
            assert(response.code() == 200)
            success = true
        }
        return true
    }
}
