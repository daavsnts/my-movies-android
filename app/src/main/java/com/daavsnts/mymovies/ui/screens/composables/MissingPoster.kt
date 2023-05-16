package com.daavsnts.mymovies.ui.screens.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.daavsnts.mymovies.R

@Composable
fun MissingPoster(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.missing_poster),
        contentDescription = stringResource(id = R.string.loading_error_content_description),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxSize()
            .alpha(0.5f)
    )
}