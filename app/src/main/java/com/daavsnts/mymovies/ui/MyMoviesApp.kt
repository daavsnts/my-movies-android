package com.daavsnts.mymovies.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import com.daavsnts.mymovies.ui.screens.movies.MoviesScreen
import com.daavsnts.mymovies.ui.screens.movies.MoviesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MyMoviesApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colorScheme.background
        ) {
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
                moviesUiStateList = moviesUiStateList,
                searchedMoviesUiStateList = searchedMoviesUiStateList,
                setSearchedMoviesList = { searchTerm: String ->
                    moviesViewModel.setSearchedMoviesList(
                        searchTerm
                    )
                }
            )
        }
    }
}