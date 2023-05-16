package com.daavsnts.mymovies.ui.screens.favoriteMovies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daavsnts.mymovies.R
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import com.daavsnts.mymovies.ui.screens.composables.RenderMoviesGrid
import com.daavsnts.mymovies.ui.screens.composables.SearchBar
import kotlinx.coroutines.delay

@Composable
fun FavoriteMoviesScreen(
    navController: NavController,
    favoriteMoviesUiState: ScreenUiState<List<Movie>>,
    searchedMoviesUiStateList: ScreenUiState<List<Movie>>,
    setSearchedMoviesList: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchTerm = remember { mutableStateOf("") }

    LaunchedEffect(searchTerm.value) {
        delay(3000)
        setSearchedMoviesList(searchTerm.value)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
    ) {
        SearchBar(searchTerm)
        Spacer(modifier.height(20.dp))
        if (favoriteMoviesUiState is ScreenUiState.Success) {
            if (favoriteMoviesUiState.data.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.fms_empty_favorite_list_text),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .alpha(0.8f)
                )
            }
        } else {
            RenderMoviesGrid(
                moviesUiStateList = if (searchTerm.value.isEmpty())
                    favoriteMoviesUiState else searchedMoviesUiStateList,
                navController = navController
            )
        }
    }
}