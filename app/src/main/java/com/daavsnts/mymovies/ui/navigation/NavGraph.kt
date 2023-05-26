package com.daavsnts.mymovies.ui.navigation

import android.content.Context
import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(navController: NavHostController) {
    AnimatedNavHost(navController = navController, startDestination = Destinations.Movies.route) {
        composable(Destinations.Movies.route) { MoviesScreenGraph() }
        composable(Destinations.Favorites.route) { FavoriteScreenGraph() }
        profileScreen()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MoviesScreenGraph(navController: NavHostController = rememberAnimatedNavController()) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Destinations.MoviesDiscover.route
    ) {
        composable(
            Destinations.MoviesDiscover.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Destinations.MoviesDetails.route ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Destinations.MoviesDetails.route ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Destinations.MoviesDetails.route ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    Destinations.MoviesDetails.route ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            }
        ) {
            val moviesViewModel = hiltViewModel<MoviesViewModel>()
            val moviesUiStateList =
                moviesViewModel.moviesUiStatesList.map {
                    Pair(
                        it.title,
                        it.list.collectAsStateWithLifecycle(initialValue = ScreenUiState.Loading).value
                    )
                }
            val searchedMoviesUiStateList =
                moviesViewModel
                    .searchedMoviesUiState
                    .collectAsStateWithLifecycle(initialValue = ScreenUiState.Loading).value

            MoviesScreen(
                navigateToDetails = { movieId: Int ->
                    navController.navigate("MovieDetailsScreen/$movieId") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
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
        composable(
            Destinations.MoviesDetails.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Destinations.MoviesDiscover.route ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Destinations.MoviesDiscover.route ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Destinations.MoviesDiscover.route ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    Destinations.MoviesDiscover.route ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            }
        ) {
            val movieDetailsViewModel = hiltViewModel<MovieDetailsViewModel>()
            val movieIdArg = it.arguments?.getString("movieId")?.toInt()
            val movieDetailsUiState =
                movieDetailsViewModel
                    .movieDetailUiState
                    .collectAsStateWithLifecycle(initialValue = ScreenUiState.Loading).value
            val isMovieFavorite =
                movieDetailsViewModel
                    .isMovieFavorite
                    .collectAsStateWithLifecycle(initialValue = false).value

            movieIdArg?.let {
                LaunchedEffect(movieIdArg) {
                    movieDetailsViewModel.setMovieDetails(movieIdArg)
                    movieDetailsViewModel.refreshIsMovieFavorite(movieIdArg)
                }

                MovieDetailsScreen(
                    popBackStack = {
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
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FavoriteScreenGraph(navController: NavHostController = rememberAnimatedNavController()) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Destinations.FavoritesDiscover.route
    ) {
        composable(
            Destinations.FavoritesDiscover.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Destinations.FavoritesDetails.route ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Destinations.FavoritesDetails.route ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Destinations.FavoritesDetails.route ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    Destinations.FavoritesDetails.route ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            }
        ) {
            val favoriteMoviesViewModel = hiltViewModel<FavoriteMoviesViewModel>()
            val favoriteMoviesUiState =
                favoriteMoviesViewModel
                    .favoriteMoviesUiState
                    .collectAsStateWithLifecycle(initialValue = ScreenUiState.Loading).value
            val searchedMoviesUiStateList =
                favoriteMoviesViewModel
                    .searchedMoviesUiState
                    .collectAsStateWithLifecycle(initialValue = ScreenUiState.Loading).value
            FavoriteMoviesScreen(
                navigateToDetails = { movieId: Int ->
                    navController.navigate("FavoriteDetailsScreen/$movieId") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
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
        composable(
            Destinations.FavoritesDetails.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Destinations.FavoritesDiscover.route ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Destinations.FavoritesDiscover.route ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Destinations.FavoritesDiscover.route ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    Destinations.FavoritesDiscover.route ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            }
        ) {
            val movieDetailsViewModel = hiltViewModel<MovieDetailsViewModel>()
            val movieIdArg = it.arguments?.getString("movieId")?.toInt()
            val movieDetailsUiState =
                movieDetailsViewModel
                    .movieDetailUiState
                    .collectAsState(initial = ScreenUiState.Loading).value
            val isMovieFavorite =
                movieDetailsViewModel
                    .isMovieFavorite
                    .collectAsStateWithLifecycle(initialValue = false).value

            movieIdArg?.let {
                LaunchedEffect(movieIdArg) {
                    movieDetailsViewModel.setMovieDetails(movieIdArg)
                    movieDetailsViewModel.refreshIsMovieFavorite(movieIdArg)
                }

                MovieDetailsScreen(
                    popBackStack = {
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
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.profileScreen() {
    composable(Destinations.Profile.route) {
        val userProfileViewModel = hiltViewModel<UserProfileViewModel>()
        val userNameUiState =
            userProfileViewModel
                .userName
                .collectAsStateWithLifecycle(initialValue = ScreenUiState.Loading).value
        val profilePictureUriUiState =
            userProfileViewModel
                .profilePictureUri
                .collectAsStateWithLifecycle(initialValue = ScreenUiState.Loading).value
        val profileBackgroundUriUiState =
            userProfileViewModel
                .profileBackgroundUri
                .collectAsStateWithLifecycle(initialValue = ScreenUiState.Loading).value
        val userFavoriteMoviesQuantityUiState =
            userProfileViewModel
                .userFavoriteMoviesQuantity
                .collectAsStateWithLifecycle(initialValue = ScreenUiState.Loading).value
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
                if (screen.title != null && screen.icon != null) {
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
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}