package com.daavsnts.mymovies.ui.screens.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.ui.screens.ScreenUiState

@Composable
fun MoviesGrid(
    moviesUiStateList: ScreenUiState<List<Movie>>,
    navController: NavController
) = RenderMoviesGrid(moviesUiStateList = moviesUiStateList, navController = navController)

@Composable
fun RenderMoviesGrid(
    moviesUiStateList: ScreenUiState<List<Movie>>,
    navController: NavController
) {
    when (moviesUiStateList) {
        is ScreenUiState.Loading -> LoadingGridOfMovies()
        is ScreenUiState.Success -> GridOfMovies(moviesUiStateList.data, navController)
        is ScreenUiState.Error -> ErrorGridOfMovies()
    }
}

@Composable
fun LoadingGridOfMovies() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(15) {
            LoadingMovieCard()
        }
    }
}

@Composable
fun GridOfMovies(
    movies: List<Movie>,
    navController: NavController
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        itemsIndexed(movies) { _, movie ->
            MovieCard(movie = movie, navController = navController)
        }
    }
}

@Composable
fun ErrorGridOfMovies() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(15) {
            ErrorMovieCard()
        }
    }
}