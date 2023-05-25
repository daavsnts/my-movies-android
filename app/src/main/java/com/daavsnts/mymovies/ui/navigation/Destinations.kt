package com.daavsnts.mymovies.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.daavsnts.mymovies.R

sealed class Destinations(
    val route: String,
    val subRoute: String?,
    @StringRes val title: Int?,
    val icon: ImageVector?
) {
    companion object {
        val navScreenList = listOf(Movies, Favorites, Profile)
    }

    object Movies : Destinations(
        "MoviesScreen",
        "MoviesDiscoverScreen",
        R.string.nav_title_movies,
        Icons.Default.PlayArrow
    )

    object MoviesDiscover : Destinations(
        "MoviesDiscoverScreen",
        null,
        null,
        null
    )

    object MoviesDetails : Destinations(
        "MovieDetailsScreen/{movieId}",
        null,
        null,
        null
    )

    object Favorites : Destinations(
        "FavoriteMoviesScreen",
        "FavoriteDiscoverScreen",
        R.string.nav_title_favorites,
        Icons.Default.Star
    )

    object FavoritesDiscover : Destinations(
        "FavoriteDiscoverScreen",
        null,
        null,
        null
    )

    object FavoritesDetails : Destinations(
        "FavoriteDetailsScreen/{movieId}",
        null,
        null,
        null
    )

    object Profile : Destinations(
        "UserProfileScreen",
        null,
        R.string.nav_title_profile,
        Icons.Default.Person
    )
}