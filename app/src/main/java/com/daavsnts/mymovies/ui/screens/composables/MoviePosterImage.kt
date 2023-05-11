package com.daavsnts.mymovies.ui.screens.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.daavsnts.mymovies.R
import com.daavsnts.mymovies.ui.screens.shimmerEffect

@Composable
fun MoviePosterImage(posterPath: String?, modifier: Modifier = Modifier) {
    val posterImage = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(posterPath)
            .crossfade(true)
            .build()
    )
    Box(modifier = modifier.background(Color.Black)) {
        when (posterImage.state) {
            is AsyncImagePainter.State.Success -> {
                // Use the loaded image
                Image(
                    painter = posterImage,
                    contentDescription = stringResource(R.string.movie_poster_content_description),
                    contentScale = ContentScale.Crop,
                    modifier = modifier.fillMaxSize()
                )
            }
            is AsyncImagePainter.State.Error -> {
                // Treat error
            }

            is AsyncImagePainter.State.Empty, null -> {
                // Not loaded yet
            }

            is AsyncImagePainter.State.Loading -> {
                // Still loading
                Image(
                    painter = posterImage,
                    contentDescription = stringResource(R.string.movie_poster_content_description),
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .fillMaxSize()
                        .alpha(0.5f)
                        .shimmerEffect()
                )
            }
        }
    }
}