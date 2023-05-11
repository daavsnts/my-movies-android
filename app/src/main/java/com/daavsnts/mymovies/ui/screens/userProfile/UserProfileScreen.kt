package com.daavsnts.mymovies.ui.screens.userProfile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.daavsnts.mymovies.R
import com.daavsnts.mymovies.ui.screens.ScreenUiState
import com.daavsnts.mymovies.ui.screens.composables.UpsideGradient


@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    userNameUiState: ScreenUiState<String>,
    profilePictureUriUiState: ScreenUiState<String>,
    profileBackgroundUriUiState: ScreenUiState<String>,
    setUserName: (String) -> Unit,
    setProfilePicture: (Context, Uri) -> Unit,
    setProfileBackground: (Context, Uri) -> Unit
) {
    var showUsernameChangeDialog by remember { mutableStateOf(false) }
    Box {
        ProfileBackground(
            profileBackgroundUriUiState = profileBackgroundUriUiState,
            setProfileBackground = setProfileBackground
        )
        UpsideGradient(startY = 0f, color = MaterialTheme.colorScheme.primary)
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
fun getGalleryActivityLauncher(
    context: Context,
    setUriFunction: (Context, Uri) -> Unit
): ManagedActivityResultLauncher<String, Uri?> =
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { setUriFunction(context, uri) }
        })

@Composable
fun ProfileBackground(
    modifier: Modifier = Modifier,
    profileBackgroundUriUiState: ScreenUiState<String>,
    setProfileBackground: (Context, Uri) -> Unit
) {
    val context = LocalContext.current
    val galleryActivityLauncher =
        getGalleryActivityLauncher(context, setProfileBackground)
    Box(modifier = modifier.fillMaxSize()) {
        when (profileBackgroundUriUiState) {
            is ScreenUiState.Loading -> Log.d("profilePictureUriUiState", "Loading")
            is ScreenUiState.Success -> {
                Log.d("profilePictureUriUiState", "Success")
                Log.d("profilePictureUriUiState", profileBackgroundUriUiState.data)
                BackgroundImage(pictureUri = profileBackgroundUriUiState.data)
            }

            is ScreenUiState.Error -> Log.d("profilePictureUriUiState", "Error")
        }
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = stringResource(R.string.ups_change_profile_background),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(20.dp)
                .size(40.dp)
                .background(MaterialTheme.colorScheme.onBackground, CircleShape)
                .border(3.dp, MaterialTheme.colorScheme.background, CircleShape)
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .clickable { galleryActivityLauncher.launch("image/*") }
        )
    }
}

fun getBitMap(context: Context, pictureUri: String): Bitmap =
    if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images
            .Media.getBitmap(context.contentResolver, pictureUri.toUri())

    } else {
        val source = ImageDecoder
            .createSource(context.contentResolver, pictureUri.toUri())
        ImageDecoder.decodeBitmap(source)
    }

@Composable
fun BackgroundImage(pictureUri: String) {
    if (pictureUri != "") {
        val bitmap = getBitMap(LocalContext.current, pictureUri)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.ups_background_picture),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ProfilePicture(
    profilePictureUriUiState: ScreenUiState<String>,
    setProfilePicture: (Context, Uri) -> Unit
) {
    val galleryActivityLauncher =
        getGalleryActivityLauncher(LocalContext.current, setProfilePicture)
    Box {
        when (profilePictureUriUiState) {
            is ScreenUiState.Loading -> Log.d("profilePictureUriUiState", "Loading")
            is ScreenUiState.Success -> {
                Log.d("profilePictureUriUiState", "Success")
                Log.d("profilePictureUriUiState", profilePictureUriUiState.data)
                ProfileImage(pictureUri = profilePictureUriUiState.data)
            }

            is ScreenUiState.Error -> Log.d("profilePictureUriUiState", "Error")
        }
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = stringResource(R.string.ups_change_profile_picture),
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
    pictureUri: String
) {
    if (pictureUri == "") {
        Icon(
            imageVector = Icons.Rounded.Person,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = stringResource(id = R.string.ups_default_profile_picture),
            modifier = modifier
                .size(200.dp)
                .background(MaterialTheme.colorScheme.onBackground, CircleShape)
        )
    } else {
        val bitmap = getBitMap(LocalContext.current, pictureUri)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(id = R.string.ups_profile_picture),
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape),
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
                    Text(stringResource(id = R.string.ups_default_username), style = MaterialTheme.typography.headlineLarge)
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
                contentDescription = stringResource(id = R.string.ups_edit_username),
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
    Text(stringResource(id = R.string.ups_favorite_movies), style = MaterialTheme.typography.bodyMedium)
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
                text = stringResource(id = R.string.ups_enter_your_username),
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
                    text = stringResource(id = R.string.ups_confirm_dialog),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(id = R.string.ups_cancel_dialog),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )
}