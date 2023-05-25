package com.daavsnts.mymovies.ui.navigation

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
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
import kotlinx.coroutines.flow.first

@Composable
fun NavGraph(navController: NavHostController) {
    val moviesScreenNavigationViewModel = hiltViewModel<MoviesScreenNavigationViewModel>()
    val moviesScreenCurrentDestination =
        moviesScreenNavigationViewModel
            .currentDestination
            .collectAsState(initial = Destinations.MoviesDiscover.route).value
    val favoriteScreenNavigationViewModel = hiltViewModel<FavoriteScreenNavigationViewModel>()
    val favoriteScreenCurrentDestination =
        favoriteScreenNavigationViewModel
            .currentDestination
            .collectAsState(initial = Destinations.FavoritesDiscover.route).value

    NavHost(navController = navController, startDestination = Destinations.Movies.route) {
        moviesScreen(navController, moviesScreenNavigationViewModel, moviesScreenCurrentDestination)
        favoriteScreen(
            navController,
            favoriteScreenNavigationViewModel,
            favoriteScreenCurrentDestination
        )
        profileScreen()
    }
}

fun NavGraphBuilder.moviesScreen(
    navController: NavHostController,
    navigationViewModel: MoviesScreenNavigationViewModel,
    currentDestination: String
) {
    navigation(
        route = Destinations.Movies.route,
        startDestination = currentDestination
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
                navigateToDetails = { movieId: Int ->
                    navigationViewModel.setDestination(Destinations.MoviesDetails.route)
                    navController.navigate("MovieDetailsScreen/$movieId")
                },
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
            val movieIdArg = it.arguments?.getString("movieId")?.toInt()
            val movieDetailsUiState =
                movieDetailsViewModel
                    .movieDetailUiState
                    .collectAsState(initial = ScreenUiState.Loading).value
            val isMovieFavorite =
                movieDetailsViewModel.isMovieFavorite.collectAsState(initial = false).value
            movieIdArg?.let { navigationViewModel.setDetailsMovieId(movieIdArg) }

            LaunchedEffect(Unit) {
                val currentMovieId =
                    navigationViewModel.currentMovieId.first()
                movieDetailsViewModel.setMovieDetails(currentMovieId)
                movieDetailsViewModel.refreshIsMovieFavorite(currentMovieId)
            }
            MovieDetailsScreen(
                popBackStack = {
                    navigationViewModel.setDestination(Destinations.MoviesDiscover.route)
                    navController.popBackStack()
                },
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

fun NavGraphBuilder.favoriteScreen(
    navController: NavHostController,
    navigationViewModel: FavoriteScreenNavigationViewModel,
    currentDestination: String
) {
    navigation(
        route = Destinations.Favorites.route,
        startDestination = currentDestination
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
                navigateToDetails = { movieId: Int ->
                    navigationViewModel.setDestination(Destinations.FavoritesDetails.route)
                    navController.navigate("FavoriteDetailsScreen/$movieId")
                },
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
            val movieIdArg = it.arguments?.getString("movieId")?.toInt()
            val movieDetailsUiState =
                movieDetailsViewModel
                    .movieDetailUiState
                    .collectAsState(initial = ScreenUiState.Loading).value
            val isMovieFavorite =
                movieDetailsViewModel.isMovieFavorite.collectAsState(initial = false).value

            movieIdArg?.let { navigationViewModel.setDetailsMovieId(movieIdArg) }

            LaunchedEffect(Unit) {
                val currentMovieId = navigationViewModel.currentMovieId.first()
                movieDetailsViewModel.setMovieDetails(currentMovieId)
                movieDetailsViewModel.refreshIsMovieFavorite(currentMovieId)
            }
            MovieDetailsScreen(
                popBackStack = {
                    navigationViewModel.setDestination(Destinations.FavoritesDiscover.route)
                    navController.popBackStack()
                },
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
                    selected = currentDestination == screen.route || currentDestination == screen.subRoute,
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