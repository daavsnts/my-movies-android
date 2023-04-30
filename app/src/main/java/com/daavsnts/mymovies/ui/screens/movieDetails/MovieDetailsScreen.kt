package com.daavsnts.mymovies.ui.screens.movieDetails

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.daavsnts.mymovies.model.Genre
import com.daavsnts.mymovies.model.Movie
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import com.daavsnts.mymovies.ui.screens.composables.CircularProgressBar
import com.daavsnts.mymovies.ui.screens.composables.MoviePosterImage
import com.daavsnts.mymovies.ui.screens.composables.UpsideGradient
import com.daavsnts.mymovies.ui.theme.Maize

@Composable
fun MovieDetailsScreen(
    navController: NavHostController,
    movieDetailsUiState: ScreenUiState<Movie>,
    addFavoriteMovie: (Movie) -> Unit,
    removeFavoriteMovie: (Movie) -> Unit,
    isMovieFavorite: Boolean
) {
    when (movieDetailsUiState) {
        is ScreenUiState.Loading -> MovieDetailsLoading()
        is ScreenUiState.Success -> {
            MovieDetails(
                navController = navController,
                movie = movieDetailsUiState.data,
                isMovieFavorite = isMovieFavorite,
                addFavoriteMovie = addFavoriteMovie,
                removeFavoriteMovie = removeFavoriteMovie
            )
        }

        is ScreenUiState.Error -> Log.d("moviesUiState", "Error")
    }
}

@Composable
fun MovieDetailsLoading(modifier: Modifier = Modifier) {
    // Create animation of circle loading
}

@Composable
fun MovieDetails(
    navController: NavHostController,
    movie: Movie,
    isMovieFavorite: Boolean,
    addFavoriteMovie: (Movie) -> Unit,
    removeFavoriteMovie: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        movie.posterPath?.let {
            MovieDetailsBackground(it)
        }
        BackButton(
            modifier = modifier
                .padding(top = 20.dp, start = 20.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(15.dp)),
            navController = navController
        )
        InfoBox(
            movie,
            isMovieFavorite = isMovieFavorite,
            addFavoriteMovie = addFavoriteMovie,
            removeFavoriteMovie = removeFavoriteMovie
        )
    }
}

@Composable
fun MovieDetailsBackground(posterPath: String) {
    MoviePosterImage(posterPath = posterPath)
    UpsideGradient(startY = 300f, MaterialTheme.colorScheme.surface)
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Box(
        modifier = modifier
            .padding(5.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "go back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun InfoBox(
    movie: Movie,
    modifier: Modifier = Modifier,
    isMovieFavorite: Boolean,
    addFavoriteMovie: (Movie) -> Unit,
    removeFavoriteMovie: (Movie) -> Unit
) {
    Box(
        modifier = modifier
            .padding(top = 280.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier
                    .fillMaxWidth()
                    .height(80.dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UserScore(movie.voteAverage)
                FavoriteButton(
                    movie = movie,
                    isMovieFavorite = isMovieFavorite,
                    addFavoriteMovie = addFavoriteMovie,
                    removeFavoriteMovie = removeFavoriteMovie
                )
            }
            Spacer(modifier.height(5.dp))
            Row {
                movie.title?.let {
                    TitleText(title = it)
                }
            }
            Spacer(modifier.height(5.dp))
            movie.releaseDate?.let {
                ReleaseDateText(it)
            }
            Spacer(modifier = modifier.height(15.dp))
            movie.genres?.let {
                GenreList(genres = it)
            }
            Spacer(modifier = modifier.height(15.dp))
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (movie.overview.isNotEmpty()) OverviewText(overview = movie.overview)
            }
        }
    }
}

@Composable
fun UserScore(voteAverage: Float, modifier: Modifier = Modifier) {
    Row(modifier.padding(top = 10.dp)) {
        CircularProgressBar(
            percentage = voteAverage,
            fontSize = 20.sp,
            radius = 30.dp
        )
        Spacer(modifier = modifier.width(12.dp))
        UserScoreText()
    }
}

@Composable
fun UserScoreText(modifier: Modifier = Modifier) {
    val userScore = listOf("User", "Score")
    Column(modifier.padding(top = 2.dp)) {
        userScore.forEach {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun TitleText(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
fun FavoriteButton(
    movie: Movie,
    isMovieFavorite: Boolean,
    addFavoriteMovie: (Movie) -> Unit,
    removeFavoriteMovie: (Movie) -> Unit,
) {
    FavoriteIcon(
        iconType = if (isMovieFavorite) Icons.Default.Star else Icons.TwoTone.Star,
        movie = movie,
        favoriteFunction = if (isMovieFavorite) removeFavoriteMovie else addFavoriteMovie
    )
}

@Composable
fun FavoriteIcon(
    iconType: ImageVector,
    movie: Movie,
    favoriteFunction: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .size(80.dp)
            .clickable(indication = rememberRipple(
                radius = 32.dp,
                color = Color.Black.copy(alpha = 0.4f)
            ),
                interactionSource = remember { MutableInteractionSource() }) {
                favoriteFunction(movie)
            }) {
        Icon(
            imageVector = iconType,
            contentDescription = null,
            tint = Maize,
            modifier = modifier.size(80.dp)
        )
    }
}

@Composable
fun ReleaseDateText(releaseDate: String) {
    Text(
        text = releaseDate,
        color = Color.LightGray,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Light
        )
    )
}

@Composable
fun OverviewText(overview: String, modifier: Modifier = Modifier) {
    val maxLines = remember { mutableStateOf(7) }

    Box(modifier.fillMaxSize()) {
        Column(
            modifier.animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {
            Box(modifier.clickable {
                if (maxLines.value == 7) maxLines.value = 20 else maxLines.value = 7
            }) {
                Text(
                    text = overview,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = maxLines.value,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Normal,
                    )
                )
            }
        }
    }
}

@Composable
fun GenreList(genres: List<Genre>, modifier: Modifier = Modifier) {
    LazyRow(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
        itemsIndexed(genres) { _, genre ->
            GenreButton(genre.name)
        }
    }
}

@Composable
fun GenreButton(genre: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(Color.DarkGray, RoundedCornerShape(5.dp))
            .padding(start = 7.dp, end = 7.dp, top = 3.dp, bottom = 3.dp)
    ) {
        Text(
            text = genre,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Light
            )
        )
    }
}