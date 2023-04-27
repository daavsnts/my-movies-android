package com.daavsnts.mymovies

import android.app.Application

class MyMoviesApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
}