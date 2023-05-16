package com.daavsnts.mymovies.ui.screens.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.daavsnts.mymovies.R
import com.daavsnts.mymovies.ui.screens.shimmerEffect

@OptIn(ExperimentalCoilApi::class)
@Composable
fun MoviePosterImage(posterPath: String?, modifier: Modifier = Modifier) {
    val imageLoader = LocalContext.current.imageLoader
    posterPath?.let {
        imageLoader.diskCache?.remove(posterPath)
        imageLoader.memoryCache?.remove(MemoryCache.Key(posterPath))
    }
    val posterImage = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(posterPath)
            .crossfade(true)
            .build()
    )
    Box(modifier = modifier.background(Color.Black)) {
        when (posterImage.state) {
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = posterImage,
                    contentDescription = stringResource(R.string.movie_poster_content_description),
                    contentScale = ContentScale.Crop,
                    modifier = modifier.fillMaxSize()
                )
                UpsideGlassGradient(
                    posterImage = posterImage,
                    startY = 300f,
                    color = MaterialTheme.colorScheme.surface
                )
            }

            is AsyncImagePainter.State.Error -> {
                MissingPoster()
                UpsideGlassGradient(
                    posterImage = painterResource(id = R.drawable.missing_poster),
                    startY = 300f,
                    color = MaterialTheme.colorScheme.surface
                )
            }

            is AsyncImagePainter.State.Empty -> {}

            is AsyncImagePainter.State.Loading -> {
                Image(
                    painter = posterImage,
                    contentDescription = stringResource(R.string.movie_poster_content_description),
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .fillMaxSize()
                        .alpha(0.5f)
                        .shimmerEffect()
                )
                UpsideGlassGradient(
                    posterImage = posterImage,
                    startY = 300f,
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}