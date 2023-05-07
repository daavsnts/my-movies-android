package com.daavsnts.mymovies.ui.screens.userProfile

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.daavsnts.mymovies.ui.screens.ScreenUiState


@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    userNameUiState: ScreenUiState<String>,
    profilePictureUriUiState: ScreenUiState<String>,
    setUserName: (String) -> Unit,
    setProfilePicture: (Context, Uri) -> Unit
) {
    var showUsernameChangeDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfilePicture(
            profilePictureUriUiState = profilePictureUriUiState,
            setProfilePicture = setProfilePicture
        )
        Spacer(modifier = modifier.height(15.dp))
        UserName(
            userNameUiState = userNameUiState,
            showDialog = { showUsernameChangeDialog = it })
        Spacer(modifier = modifier.height(15.dp))
        UserAnalytics()
    }
    if (showUsernameChangeDialog) {
        EditTextDialog(
            onConfirmClick = { username ->
                setUserName(username)
                showUsernameChangeDialog = false
            },
            onDismissRequest = { showUsernameChangeDialog = false }
        )
    }
}

@Composable
fun ProfilePicture(
    modifier: Modifier = Modifier,
    profilePictureUriUiState: ScreenUiState<String>,
    setProfilePicture: (Context, Uri) -> Unit
) {
    val context = LocalContext.current
    val galleryActivityLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                uri?.let { setProfilePicture(context, uri) }
            })
    Box {
        when (profilePictureUriUiState) {
            is ScreenUiState.Loading -> Log.d("profilePictureUriUiState", "Loading")
            is ScreenUiState.Success -> {
                Log.d("profilePictureUriUiState", "Success")
                Log.d("profilePictureUriUiState", profilePictureUriUiState.data)
                ProfileImage(profilePictureUri = profilePictureUriUiState.data)
            }

            is ScreenUiState.Error -> Log.d("profilePictureUriUiState", "Error")
        }
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = "Change profile picture",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onBackground, CircleShape)
                .border(3.dp, MaterialTheme.colorScheme.background, CircleShape)
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .clickable { galleryActivityLauncher.launch("image/*") }
        )
    }
}

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    profilePictureUri: String
) {
    Log.d("profileImage", profilePictureUri)
    val context = LocalContext.current
    if (profilePictureUri == "") {
        Icon(
            imageVector = Icons.Rounded.Person,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Default profile picture",
            modifier = modifier
                .size(200.dp).background(MaterialTheme.colorScheme.onBackground, CircleShape)
        )
    } else {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images
                .Media.getBitmap(context.contentResolver, profilePictureUri.toUri())

        } else {
            val source = ImageDecoder
                .createSource(context.contentResolver, profilePictureUri.toUri())
            ImageDecoder.decodeBitmap(source)
        }
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Profile picture",
            modifier = Modifier.size(200.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun UserName(
    modifier: Modifier = Modifier,
    userNameUiState: ScreenUiState<String>,
    showDialog: (Boolean) -> Unit
) {
    Row {
        Spacer(modifier = modifier.width(20.dp))
        when (userNameUiState) {
            is ScreenUiState.Loading -> Log.d("profilePictureUriUiState", "Loading")
            is ScreenUiState.Success -> {
                if (userNameUiState.data == "") {
                    Text("Username", style = MaterialTheme.typography.headlineLarge)
                } else {
                    Text(userNameUiState.data, style = MaterialTheme.typography.headlineLarge)
                }
            }

            is ScreenUiState.Error -> Log.d("profilePictureUriUiState", "Error")
        }
        Spacer(modifier = modifier.width(5.dp))
        Box(modifier = modifier
            .background(MaterialTheme.colorScheme.onBackground, CircleShape)
            .padding(2.dp)
            .clickable {
                showDialog(true)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit username",
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier.size(15.dp)
            )
        }
    }
}

@Composable
fun UserAnalytics(
    modifier: Modifier = Modifier
) {
    Text("Favorite Movies", style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = modifier.height(5.dp))
    Text(
        "45", style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun EditTextDialog(
    modifier: Modifier = Modifier,
    onConfirmClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var userName by remember { mutableStateOf(TextFieldValue()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Digite seu nome de usuário",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        text = {
            Box(
                modifier = modifier
                    .background(
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(15.dp)
                    )
                    .fillMaxWidth()
            ) {
                BasicTextField(
                    value = userName,
                    onValueChange = {
                        userName = it
                    },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    singleLine = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmClick(userName.text) }) {
                Text(
                    text = "Ok",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )
}