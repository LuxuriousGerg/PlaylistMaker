package com.example.playlistmaker.di

import PlayerViewModel
import SearchViewModel
import android.app.Application
import com.example.playlistmaker.data.network.iTunesApiService
import com.example.playlistmaker.data.repository.*
import com.example.playlistmaker.domain.interactors.*
import com.example.playlistmaker.domain.repository.*
import com.example.playlistmaker.presentation.viewmodel.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(dataModule, domainModule, viewModelModule))
        }
    }
}

val dataModule: Module = module {
    single<iTunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApiService::class.java)
    }

    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<HistoryRepository> { SearchHistory(get()) }
    single<PlayerRepository> { PlayerRepositoryImpl() }

    single {
        androidContext().getSharedPreferences("playlist_prefs", android.content.Context.MODE_PRIVATE)
    }
}

val domainModule: Module = module {
    factory<SearchTracksInteractor> { SearchTracksInteractorImpl(get()) }
    factory<SettingsInteractor> { SettingsInteractorImpl(get()) }
    factory<HistoryInteractor> { HistoryInteractorImpl(get()) }
    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }
}

val viewModelModule: Module = module {
    viewModel { PlayerViewModel(get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
}


