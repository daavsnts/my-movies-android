package com.daavsnts.mymovies.ui.screens.composables

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.ui.screens.ScreenUiState

@Composable
fun MoviesLists(
    modifier: Modifier = Modifier,
    moviesUiStateList: List<Pair<String, ScreenUiState<List<Movie>>>>
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
        itemsIndexed(moviesUiStateList) { _, list ->
            HeaderText(list.first)
            Spacer(modifier.height(10.dp))
            RenderMoviesList(moviesUiState = list.second)
        }
    }
}

@Composable
fun HeaderText(text: String) {
    Text(
        text = text,
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun RenderMoviesList(
    moviesUiState: ScreenUiState<List<Movie>>
) {
    when (moviesUiState) {
        is ScreenUiState.Loading -> LoadingListOfMovies()
        is ScreenUiState.Success -> ListOfMovies(moviesUiState.data)
        is ScreenUiState.Error -> Log.d("moviesUiState", "Error")
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
fun ListOfMovies(movies: List<Movie>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        itemsIndexed(movies) { _, movie ->
            MovieCard(movie = movie)
        }
    }
}