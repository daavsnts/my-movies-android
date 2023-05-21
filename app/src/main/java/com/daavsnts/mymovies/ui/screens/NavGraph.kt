package com.daavsnts.mymovies.ui.screens

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.daavsnts.mymovies.R
import com.daavsnts.mymovies.domain.model.Movie
import com.daavsnts.mymovies.ui.screens.favoriteMovies.FavoriteMoviesScreen
import com.daavsnts.mymovies.ui.screens.favoriteMovies.FavoriteMoviesViewModel
import com.daavsnts.mymovies.ui.screens.movieDetails.MovieDetailsScreen
import com.daavsnts.mymovies.ui.screens.movieDetails.MovieDetailsViewModel
import com.daavsnts.mymovies.ui.screens.movies.MoviesScreen
import com.daavsnts.mymovies.ui.screens.movies.MoviesViewModel
import com.daavsnts.mymovies.ui.screens.userProfile.UserProfileScreen
import com.daavsnts.mymovies.ui.screens.userProfile.UserProfileViewModel
import com.daavsnts.mymovies.ui.theme.GoogleSans
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

sealed class Screen(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector
) {
    companion object {
        val navScreenList = listOf(Movies, Favorites, Profile)
    }

    object Movies : Screen(
        "MoviesScreen",
        R.string.nav_title_movies,
        Icons.Default.PlayArrow
    )

    object MoviesDiscover : Screen(
        "MoviesDiscoverScreen",
        R.string.nav_title_movies,
        Icons.Default.PlayArrow
    )

    object MoviesDetails : Screen(
        "MoviesDetailsScreen/{movieId}",
        R.string.nav_title_details,
        Icons.Default.List
    )

    object Favorites : Screen(
        "FavoriteMoviesScreen",
        R.string.nav_title_favorites,
        Icons.Default.Star
    )

    object FavoritesDiscover : Screen(
        "FavoriteDiscover",
        R.string.nav_title_movies,
        Icons.Default.PlayArrow
    )

    object FavoritesDetails : Screen(
        "FavoriteDetailsScreen/{movieId}",
        R.string.nav_title_details,
        Icons.Default.List
    )

    object Profile : Screen(
        "UserProfileScreen",
        R.string.nav_title_profile,
        Icons.Default.Person
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(navController: NavHostController) {
    AnimatedNavHost(navController = navController, startDestination = Screen.Movies.route) {
        moviesScreenGraph(navController)
        favoritesScreenGraph(navController)
        composable(Screen.Profile.route) {
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
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.moviesScreenGraph(navController: NavController) {
    navigation(startDestination = Screen.MoviesDiscover.route, route = Screen.Movies.route) {
        composable(Screen.MoviesDiscover.route) {
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
                navigateToDetails = { movieId: Int -> navController.navigate("MoviesDetailsScreen/$movieId") },
                moviesUiStateList = moviesUiStateList,
                searchedMoviesUiStateList = searchedMoviesUiStateList,
                setSearchedMoviesList = { searchTerm: String ->
                    moviesViewModel.setSearchedMoviesList(
                        searchTerm
                    )
                }
            )
        }
        composable(Screen.MoviesDetails.route) {
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

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.favoritesScreenGraph(navController: NavController) {
    navigation(startDestination = Screen.FavoritesDiscover.route, route = Screen.Favorites.route) {
        composable(Screen.FavoritesDiscover.route) {
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
        composable(Screen.FavoritesDetails.route) {
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

@Composable
fun BottomNavBar(navController: NavHostController, modifier: Modifier = Modifier) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination?.route

    Box(modifier.background(MaterialTheme.colorScheme.primary)) {
        NavigationBar(containerColor = Color.Transparent) {
            Screen.navScreenList.forEach { screen ->
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