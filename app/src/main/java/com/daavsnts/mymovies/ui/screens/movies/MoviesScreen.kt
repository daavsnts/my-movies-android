package com.daavsnts.mymovies.ui.screens.movies

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daavsnts.mymovies.ui.screens.composables.MoviesGrid
import com.daavsnts.mymovies.ui.screens.composables.SearchBar
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import com.daavsnts.mymovies.ui.screens.composables.MoviesLists
import kotlinx.coroutines.delay

@Composable
fun MoviesScreen(
    modifier: Modifier = Modifier,
    moviesUiStateList: List<Pair<String, ScreenUiState<List<Movie>>>>,
    searchedMoviesUiStateList: ScreenUiState<List<Movie>>,
    setSearchedMoviesList: (String) -> Unit
) {
    val searchTerm = remember { mutableStateOf("") }

    LaunchedEffect(searchTerm.value) {
        delay(3000)
        setSearchedMoviesList(searchTerm.value)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
    ) {
        SearchBar(searchTerm)
        Spacer(modifier.height(20.dp))
        if (searchTerm.value.isEmpty()) {
            MoviesLists(moviesUiStateList = moviesUiStateList)
        } else {
            MoviesGrid(searchedMoviesUiStateList)
        }
    }
}