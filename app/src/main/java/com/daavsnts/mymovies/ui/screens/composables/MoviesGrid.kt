package com.daavsnts.mymovies.ui.screens.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.daavsnts.mymovies.domain.model.Movie
import com.daavsnts.mymovies.ui.screens.ScreenUiState

@Composable
fun MoviesGrid(
    moviesUiStateList: ScreenUiState<List<Movie>>,
    navigateToDetails: (Int) -> Unit
) = RenderMoviesGrid(moviesUiStateList = moviesUiStateList, navigateToDetails = navigateToDetails)

@Composable
fun RenderMoviesGrid(
    moviesUiStateList: ScreenUiState<List<Movie>>,
    navigateToDetails: (Int) -> Unit
) {
    when (moviesUiStateList) {
        is ScreenUiState.Loading -> LoadingGridOfMovies()
        is ScreenUiState.Success -> GridOfMovies(moviesUiStateList.data, navigateToDetails)
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
    navigateToDetails: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        itemsIndexed(movies) { _, movie ->
            MovieCard(movie = movie, navigateToDetails = navigateToDetails)
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