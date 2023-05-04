package com.daavsnts.mymovies

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.daavsnts.mymovies.repository.LocalUserRepository
import com.daavsnts.mymovies.data.local.UserDatabase
import com.daavsnts.mymovies.repository.UserRepository
import com.daavsnts.mymovies.data.network.MoviesApiService
import com.daavsnts.mymovies.repository.MoviesRepository
import com.daavsnts.mymovies.data.network.MoviesRetrofitBuilder
import com.daavsnts.mymovies.repository.DataStoreRepository
import com.daavsnts.mymovies.repository.NetworkMoviesRepository
import com.daavsnts.mymovies.repository.SettingsRepository

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

interface AppContainer {
    val moviesRepository: MoviesRepository
    val userRepository: UserRepository
    val settingsRepository: DataStoreRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val moviesRetrofitService: MoviesApiService by lazy { MoviesRetrofitBuilder.apiService }
    private val userDatabase: UserDatabase by lazy { UserDatabase.getDatabase(context) }

    override val moviesRepository: MoviesRepository by lazy {
        NetworkMoviesRepository(moviesRetrofitService)
    }

    override val userRepository: UserRepository by lazy {
        LocalUserRepository(userDatabase.userDao())
    }

    override val settingsRepository: DataStoreRepository by lazy {
        SettingsRepository(context.settingsDataStore)
    }
}