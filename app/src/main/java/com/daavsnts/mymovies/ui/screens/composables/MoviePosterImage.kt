package com.daavsnts.mymovies.ui.screens.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            }

            is AsyncImagePainter.State.Error -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                        .fillMaxSize()
                        .alpha(0.5f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = stringResource(R.string.movie_poster_loading_error_content_description),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = modifier
                            .size(50.dp)
                    )
                    Text(
                        stringResource(R.string.movie_poster_loading_error_text),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        ),
                        modifier = modifier.padding(top = 10.dp, start = 20.dp, end = 20.dp)
                    )
                }
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
            }
        }
    }
}