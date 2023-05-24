package com.daavsnts.mymovies.ui.navigation

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.daavsnts.mymovies.R
import com.daavsnts.mymovies.domain.model.Movie
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import com.daavsnts.mymovies.ui.screens.favoriteMovies.FavoriteMoviesScreen
import com.daavsnts.mymovies.ui.screens.favoriteMovies.FavoriteMoviesViewModel
import com.daavsnts.mymovies.ui.screens.movieDetails.MovieDetailsScreen
import com.daavsnts.mymovies.ui.screens.movieDetails.MovieDetailsViewModel
import com.daavsnts.mymovies.ui.screens.movies.MoviesScreen
import com.daavsnts.mymovies.ui.screens.movies.MoviesViewModel
import com.daavsnts.mymovies.ui.screens.userProfile.UserProfileScreen
import com.daavsnts.mymovies.ui.screens.userProfile.UserProfileViewModel
import com.daavsnts.mymovies.ui.theme.GoogleSans

sealed class Destinations(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector
) {
    companion object {
        val navScreenList = listOf(Movies, Favorites, Profile)
    }

    object Movies : Destinations(
        "MoviesScreen",
        R.string.nav_title_movies,
        Icons.Default.PlayArrow
    )

    object MoviesDiscover : Destinations(
        "MoviesDiscoverScreen",
        R.string.nav_title_movies,
        Icons.Default.PlayArrow
    )

    object MoviesDetails : Destinations(
        "MovieDetailsScreen/{movieId}",
        R.string.nav_title_movies,
        Icons.Default.PlayArrow
    )

    object Favorites : Destinations(
        "FavoriteMoviesScreen",
        R.string.nav_title_favorites,
        Icons.Default.Star
    )

    object FavoritesDiscover : Destinations(
        "FavoriteDiscoverScreen",
        R.string.nav_title_favorites,
        Icons.Default.Star
    )

    object FavoritesDetails : Destinations(
        "FavoriteDetailsScreen/{movieId}",
        R.string.nav_title_favorites,
        Icons.Default.Star
    )

    object Profile : Destinations(
        "UserProfileScreen",
        R.string.nav_title_profile,
        Icons.Default.Person
    )
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destinations.Movies.route) {
        moviesScreen(navController)
        favoriteScreen(navController)
        profileScreen()
    }
}

fun NavGraphBuilder.moviesScreen(navController: NavHostController) {
    navigation(
        route = Destinations.Movies.route,
        startDestination = Destinations.MoviesDiscover.route
    ) {
        composable(Destinations.MoviesDiscover.route) {
            val moviesViewModel = hiltViewModel<MoviesViewModel>()
            val moviesUiStateList =
                moviesViewModel.moviesUiStatesList.map {
                    Pair(
                        it.title,
                        it.list.collectAsState(initial = ScreenUiState.Loading).value
                    )
                }
            val searchedMoviesUiStateList =
                moviesViewModel
                    .searchedMoviesUiState
                    .collectAsState(initial = ScreenUiState.Loading).value

            MoviesScreen(
                navigateToDetails = { movieId: Int -> navController.navigate("MovieDetailsScreen/$movieId") },
                moviesUiStateList = moviesUiStateList,
                searchedMoviesUiStateList = searchedMoviesUiStateList,
                setSearchedMoviesList = { searchTerm: String ->
                    moviesViewModel.setSearchedMoviesList(
                        searchTerm
                    )
                }
            )
        }
        composable(Destinations.MoviesDetails.route) {
            val movieDetailsViewModel = hiltViewModel<MovieDetailsViewModel>()
            val movieId = it.arguments?.getString("movieId")?.toInt()
            val movieDetailsUiState =
                movieDetailsViewModel
                    .movieDetailUiState
                    .collectAsState(initial = ScreenUiState.Loading).value
            val isMovieFavorite =
                movieDetailsViewModel.isMovieFavorite.collectAsState(initial = false).value

            movieId?.let {
                LaunchedEffect(movieId) {
                    movieDetailsViewModel.setMovieDetails(movieId)
                    movieDetailsViewModel.refreshIsMovieFavorite(movieId)
                }
                MovieDetailsScreen(
                    navController = navController,
                    movieDetailsUiState = movieDetailsUiState,
                    isMovieFavorite = isMovieFavorite,
                    addFavoriteMovie = { movie: Movie ->
                        movieDetailsViewModel.addFavoriteMovie(
                            movie
                        )
                    },
                    removeFavoriteMovie = { movie: Movie ->
                        movieDetailsViewModel.removeFavoriteMovie(
                            movie
                        )
                    },
                )
            }
        }
    }
}

fun NavGraphBuilder.favoriteScreen(navController: NavHostController) {
    navigation(
        route = Destinations.Favorites.route,
        startDestination = Destinations.FavoritesDiscover.route
    ) {
        composable(Destinations.FavoritesDiscover.route) {
            val favoriteMoviesViewModel = hiltViewModel<FavoriteMoviesViewModel>()
            val favoriteMoviesUiState =
                favoriteMoviesViewModel
                    .favoriteMoviesUiState
                    .collectAsState(initial = ScreenUiState.Loading).value
            val searchedMoviesUiStateList =
                favoriteMoviesViewModel
                    .searchedMoviesUiState
                    .collectAsState(initial = ScreenUiState.Loading).value
            FavoriteMoviesScreen(
                navigateToDetails = { movieId: Int -> navController.navigate("FavoriteDetailsScreen/$movieId") },
                favoriteMoviesUiState = favoriteMoviesUiState,
                searchedMoviesUiStateList = searchedMoviesUiStateList,
                setSearchedMoviesList = { searchTerm: String ->
                    favoriteMoviesViewModel.setSearchedMoviesList(
                        searchTerm
                    )
                }
            )
        }
        composable(Destinations.FavoritesDetails.route) {
            val movieDetailsViewModel = hiltViewModel<MovieDetailsViewModel>()
            val movieId = it.arguments?.getString("movieId")?.toInt()
            val movieDetailsUiState =
                movieDetailsViewModel
                    .movieDetailUiState
                    .collectAsState(initial = ScreenUiState.Loading).value
            val isMovieFavorite =
                movieDetailsViewModel.isMovieFavorite.collectAsState(initial = false).value

            movieId?.let {
                LaunchedEffect(movieId) {
                    movieDetailsViewModel.setMovieDetails(movieId)
                    movieDetailsViewModel.refreshIsMovieFavorite(movieId)
                }
                MovieDetailsScreen(
                    navController = navController,
                    movieDetailsUiState = movieDetailsUiState,
                    isMovieFavorite = isMovieFavorite,
                    addFavoriteMovie = { movie: Movie ->
                        movieDetailsViewModel.addFavoriteMovie(
                            movie
                        )
                    },
                    removeFavoriteMovie = { movie: Movie ->
                        movieDetailsViewModel.removeFavoriteMovie(
                            movie
                        )
                    },
                )
            }
        }
    }
}

fun NavGraphBuilder.profileScreen() {
    composable(Destinations.Profile.route) {
        val userProfileViewModel = hiltViewModel<UserProfileViewModel>()
        val userNameUiState =
            userProfileViewModel
                .userName
                .collectAsState(initial = ScreenUiState.Loading).value
        val profilePictureUriUiState =
            userProfileViewModel
                .profilePictureUri
                .collectAsState(initial = ScreenUiState.Loading).value
        val profileBackgroundUriUiState =
            userProfileViewModel
                .profileBackgroundUri
                .collectAsState(initial = ScreenUiState.Loading).value
        val userFavoriteMoviesQuantityUiState =
            userProfileViewModel
                .userFavoriteMoviesQuantity
                .collectAsState(initial = ScreenUiState.Loading).value
        UserProfileScreen(
            userNameUiState = userNameUiState,
            profilePictureUriUiState = profilePictureUriUiState,
            profileBackgroundUriUiState = profileBackgroundUriUiState,
            userFavoriteMoviesQuantityUiState = userFavoriteMoviesQuantityUiState,
            setUserName = { userName: String ->
                userProfileViewModel.setUserName(userName)
            },
            setProfilePicture = { context: Context, profilePictureUri: Uri ->
                userProfileViewModel.setProfilePictureUri(context, profilePictureUri)
            },
            setProfileBackground = { context: Context, profileBackgroundUri: Uri ->
                userProfileViewModel.setBackgroundPictureUri(context, profileBackgroundUri)
            }
        )
    }
}

@Composable
fun BottomNavBar(navController: NavHostController, modifier: Modifier = Modifier) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination?.route

    Box(modifier.background(MaterialTheme.colorScheme.primary)) {
        NavigationBar(containerColor = Color.Transparent) {
            Destinations.navScreenList.forEach { screen ->
                NavigationBarItem(
                    label = { Text(stringResource(screen.title), fontFamily = GoogleSans) },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = stringResource(screen.title)
                        )
                    },
                    selected = currentDestination == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}