package com.example.playlistmaker

import android.app.Application
import android.util.Log
import com.example.playlistmaker.di.createPlaylistModule
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.domainModule
import com.example.playlistmaker.di.favoritesModule
import com.example.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(dataModule, domainModule, viewModelModule, favoritesModule,createPlaylistModule,))
        }
    }
}
