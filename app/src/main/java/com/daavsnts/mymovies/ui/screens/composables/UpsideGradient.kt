package com.daavsnts.mymovies.ui.screens.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun UpsideGradient(startY: Float, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        color,
                        color
                    ),
                    startY = startY
                )
            )
    )
}

@Composable
fun UpsideGlassGradient(
    modifier: Modifier = Modifier,
    startY: Float,
    color: Color,
    posterImage: Painter
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = posterImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                // Workaround to enable alpha compositing
                .graphicsLayer { alpha = 0.99f }
                .drawWithContent {
                    val colors = listOf(
                        Color.Transparent,
                        Color.Black,
                        Color.Black
                    )
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(colors, startY = startY),
                        blendMode = BlendMode.DstIn
                    )
                }
                .blur(20.dp)
                .alpha(0.8f)
        )
        Box(
            modifier = modifier
                .fillMaxSize()
                .alpha(0.7f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            color,
                            color
                        ),
                        startY = startY
                    )
                )
        )
    }
}