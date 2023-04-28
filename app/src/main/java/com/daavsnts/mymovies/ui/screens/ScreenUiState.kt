package com.daavsnts.mymovies.ui.screens

sealed interface ScreenUiState <out T> {
    data class Success<out T>(val data: T) : ScreenUiState<T>
    object Loading : ScreenUiState<Nothing>
    object Error: ScreenUiState<Nothing>
}