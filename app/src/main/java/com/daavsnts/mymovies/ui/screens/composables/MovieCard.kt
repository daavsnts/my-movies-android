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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.daavsnts.mymovies.model.Movie

@Composable
fun MovieCard(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(5.dp)
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
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        modifier = modifier.padding(bottom = 20.dp)
                    )
                }
                movie.releaseDate?.let {
                    Text(
                        text = it,
                        color = Color.LightGray,
                        fontSize = 15.sp
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
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Box(
            modifier = modifier
                .height(230.dp)
                .width(170.dp)
                .background(Color.Black)
                .shimmerEffect()
        )
    }
}