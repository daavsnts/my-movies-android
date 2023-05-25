package com.daavsnts.mymovies.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.daavsnts.mymovies.BuildConfig
import com.daavsnts.mymovies.data.local.room.UserDatabase
import com.daavsnts.mymovies.data.network.ApiKeyInterceptor
import com.daavsnts.mymovies.data.network.MoviesApiService
import com.daavsnts.mymovies.data.repository.LocalUserRepository
import com.daavsnts.mymovies.data.repository.NetworkMoviesRepository
import com.daavsnts.mymovies.domain.repository.MoviesRepository
import com.daavsnts.mymovies.domain.repository.UserRepository
import com.daavsnts.mymovies.ui.navigation.Destinations
import com.daavsnts.mymovies.ui.navigation.FavoriteScreenNavigationViewModel
import com.daavsnts.mymovies.ui.navigation.MoviesScreenNavigationViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    @Provides
    @Singleton
    fun provideMoviesApiService(): MoviesApiService {
        val baseUrl = "https://api.themoviedb.org/3/"
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(BuildConfig.API_KEY))
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoviesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMoviesRepository(moviesApiService: MoviesApiService): MoviesRepository =
        NetworkMoviesRepository(moviesApiService)

    @Provides
    @Singleton
    fun provideUserRepository(context: Application): UserRepository = LocalUserRepository(
        UserDatabase.getDatabase(context).userDao(),
        context.settingsDataStore
    )

    @Provides
    @Singleton
    fun provideMoviesScreenNavigationViewModel():  MoviesScreenNavigationViewModel =
        MoviesScreenNavigationViewModel(Destinations.MoviesDiscover.route)

    @Provides
    @Singleton
    fun provideFavoriteScreenNavigationViewModel(): FavoriteScreenNavigationViewModel =
        FavoriteScreenNavigationViewModel(Destinations.FavoritesDiscover.route)
}