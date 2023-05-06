package com.daavsnts.mymovies.ui.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.ui.screens.favoriteMovies.FavoriteMoviesScreen
import com.daavsnts.mymovies.ui.screens.favoriteMovies.FavoriteMoviesViewModel
import com.daavsnts.mymovies.ui.screens.movieDetails.MovieDetailsScreen
import com.daavsnts.mymovies.ui.screens.movieDetails.MovieDetailsViewModel
import com.daavsnts.mymovies.ui.screens.movies.MoviesScreen
import com.daavsnts.mymovies.ui.screens.movies.MoviesViewModel
import com.daavsnts.mymovies.ui.screens.userProfile.UserProfileScreen
import com.daavsnts.mymovies.ui.screens.userProfile.UserProfileViewModel
import com.daavsnts.mymovies.ui.theme.GoogleSans

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    companion object {
        val navScreenList = listOf(Movies, Favorites, Profile)
    }

    object Movies : Screen(
        "MoviesScreen",
        "Movies",
        Icons.Default.PlayArrow
    )

    object Details : Screen(
        "MovieDetailsScreen/{movieId}",
        "Details",
        Icons.Default.List
    )

    object Favorites : Screen(
        "FavoriteMoviesScreen",
        "Favorites",
        Icons.Default.Star
    )

    object Profile : Screen(
        "UserProfileScreen",
        "Profile",
        Icons.Default.Person
    )
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Profile.route) {
        composable(Screen.Movies.route) {
            val moviesViewModel: MoviesViewModel =
                viewModel(factory = MoviesViewModel.Factory)
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
                navController = navController,
                moviesUiStateList = moviesUiStateList,
                searchedMoviesUiStateList = searchedMoviesUiStateList,
                setSearchedMoviesList = { searchTerm: String ->
                    moviesViewModel.setSearchedMoviesList(
                        searchTerm
                    )
                }
            )
        }
        composable(Screen.Details.route) {
            val movieDetailsViewModel: MovieDetailsViewModel =
                viewModel(factory = MovieDetailsViewModel.Factory)
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
        composable(Screen.Favorites.route) {
            val favoriteMoviesViewModel: FavoriteMoviesViewModel =
                viewModel(factory = FavoriteMoviesViewModel.Factory)
            val favoriteMoviesUiState =
                favoriteMoviesViewModel
                    .favoriteMoviesUiState
                    .collectAsState(initial = ScreenUiState.Loading).value
            val searchedMoviesUiStateList =
                favoriteMoviesViewModel
                    .searchedMoviesUiState
                    .collectAsState(initial = ScreenUiState.Loading).value
            FavoriteMoviesScreen(
                navController = navController,
                favoriteMoviesUiState = favoriteMoviesUiState,
                searchedMoviesUiStateList = searchedMoviesUiStateList,
                setSearchedMoviesList = { searchTerm: String ->
                    favoriteMoviesViewModel.setSearchedMoviesList(
                        searchTerm
                    )
                }
            )
        }
        composable(Screen.Profile.route) {
            val userProfileViewModel: UserProfileViewModel =
                viewModel(factory = UserProfileViewModel.Factory)
            val userNameUiState =
                userProfileViewModel
                    .userName
                    .collectAsState(initial = ScreenUiState.Loading).value
            val profilePictureUriUiState =
                userProfileViewModel
                    .profilePictureUri
                    .collectAsState(initial = ScreenUiState.Loading).value
            UserProfileScreen(
                userNameUiState = userNameUiState,
                profilePictureUriUiState = profilePictureUriUiState,
                setUserName = { userName: String ->
                    userProfileViewModel.setUserName(userName)
                },
                setProfilePicture = {profilePictureUri: String ->
                    userProfileViewModel.setProfilePictureUri(profilePictureUri)
                }
            )
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
                    label = { Text(screen.title, fontFamily = GoogleSans) },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title
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