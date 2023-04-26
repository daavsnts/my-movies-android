package com.daavsnts.mymovies.model
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

data class FavoriteMovieId(
    val id: Int,
    val title: String?,
)

@Serializable
data class Movie(
    val id: Int,
    val title: String?,
    val overview: String,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("poster_path")
    var posterPath: String?,
    @SerializedName("vote_average")
    val voteAverage: Float,
    @SerializedName("vote_count")
    val voteCount: Int,
    val genres: List<Genre>?
)

@Serializable
data class MovieList(
    val page: Int,
    val results: List<Movie>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)
