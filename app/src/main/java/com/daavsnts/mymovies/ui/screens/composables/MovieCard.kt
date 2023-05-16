package com.daavsnts.mymovies.ui.screens.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.navigation.NavController
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.ui.screens.shimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCard(
    movie: Movie,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        onClick = {
            val movieId = movie.id
            movieId.let {
                navController.navigate("MovieDetailsScreen/$movieId")
            }
        }
    ) {
        Box(
            modifier = modifier
                .height(230.dp)
                .width(170.dp)
        ) {
            Box(
                modifier = modifier
                    .height(220.dp)
                    .fillMaxWidth()
            ) {
                MoviePosterImage(posterPath = movie.posterPath)
            }
            UpsideGradient(startY = 300f, color = MaterialTheme.colorScheme.surface)
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(15.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopStart
                ) {
                    CircularProgressBar(
                        percentage = movie.voteAverage,
                        radius = 20.dp,
                        fontSize = 15.sp
                    )
                }
                movie.title?.let {
                    Text(
                        text = it,
                        modifier = modifier.padding(bottom = 24.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge.copy(
                            shadow = Shadow(
                                Color.Black,
                                blurRadius = 8f,
                                offset = Offset(4f, 4f)
                            )
                        )
                    )
                }
                movie.releaseDate?.let {
                    Text(
                        text = it,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            shadow = Shadow(
                                Color.Black, blurRadius = 8f,
                                offset = Offset(4f, 4f)
                            )
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingMovieCard(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        modifier = modifier.alpha(0.5f)
    ) {
        Box(
            modifier = modifier
                .height(230.dp)
                .width(170.dp)
                .background(MaterialTheme.colorScheme.surface)
                .shimmerEffect()
        )
    }
}

@Composable
fun ErrorMovieCard(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        modifier = modifier.alpha(0.5f)
    ) {
        Box(
            modifier = modifier
                .height(230.dp)
                .width(170.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) { ErrorMessage(iconSize = 50.dp, textSize = 18.sp) }
    }
}