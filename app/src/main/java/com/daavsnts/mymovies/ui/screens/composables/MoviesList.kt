package com.daavsnts.mymovies.ui.screens.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daavsnts.mymovies.domain.model.Movie
import com.daavsnts.mymovies.ui.screens.ScreenUiState

@Composable
fun MoviesLists(
    modifier: Modifier = Modifier,
    moviesUiStateList: List<Pair<Int, ScreenUiState<List<Movie>>>>,
    navController: NavController
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
        itemsIndexed(moviesUiStateList) { _, list ->
            HeaderText(stringResource(list.first))
            Spacer(modifier.height(10.dp))
            RenderMoviesList(moviesUiState = list.second, navController = navController)
        }
    }
}

@Composable
fun HeaderText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun RenderMoviesList(
    moviesUiState: ScreenUiState<List<Movie>>,
    navController: NavController
) {
    when (moviesUiState) {
        is ScreenUiState.Loading -> LoadingListOfMovies()
        is ScreenUiState.Success -> ListOfMovies(moviesUiState.data, navController = navController)
        is ScreenUiState.Error -> ErrorListOfMovies()
    }
}

@Composable
fun LoadingListOfMovies() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(15) {
            LoadingMovieCard()
        }
    }
}

@Composable
fun ListOfMovies(movies: List<Movie>, navController: NavController) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        itemsIndexed(movies) { _, movie ->
            MovieCard(movie = movie, navController = navController)
        }
    }
}

@Composable
fun ErrorListOfMovies() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(15) {
            ErrorMovieCard()
        }
    }
}