package com.daavsnts.mymovies.ui.screens.userProfile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daavsnts.mymovies.R


@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box() {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "Mito",
                modifier = modifier.size(200.dp).clip(CircleShape)
            )
            Image(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Descrição do ícone",
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .align(Alignment.BottomEnd) // Posiciona no canto inferior direito
                    .padding(16.dp) // Adiciona um espaçamento para o ícone
            )
        }
        Spacer(modifier = modifier.height(15.dp))
        Text("Username", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = modifier.height(15.dp))
    }
}