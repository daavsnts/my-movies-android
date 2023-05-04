package com.daavsnts.mymovies.ui.screens.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchBar(
    searchTerm: MutableState<String>,
    modifier: Modifier = Modifier
) {
    val label = remember { mutableStateOf("Search...") }
    Box(
        modifier = modifier.background(
            MaterialTheme.colorScheme.surface,
            RoundedCornerShape(15.dp)
        ),
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = modifier.padding(15.dp)
            )
            Box {
                Text(
                    text = label.value,
                    color = Color.DarkGray,
                    modifier = modifier.padding(15.dp),
                    fontSize = 16.sp
                )
                BasicTextField(
                    value = searchTerm.value,
                    onValueChange = {
                        searchTerm.value = it
                        if (searchTerm.value !== "") label.value = "" else label.value = "Search..."
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
        }
    }
}