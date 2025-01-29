package com.example.playlistmaker.di

import PlayerViewModel
import SearchViewModel
import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmaker.data.network.iTunesApiService
import com.example.playlistmaker.data.repository.*
import com.example.playlistmaker.domain.interactors.*
import com.example.playlistmaker.domain.repository.*
import com.example.playlistmaker.presentation.viewmodel.*
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule: Module = module {
    single { Gson() }
    factory { MediaPlayer() }

    single<iTunesApiService> {
        val service = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApiService::class.java)

        Log.d("DI", "Создан iTunesApiService: $service")
        service
    }

    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<HistoryRepository> { SearchHistory(get(), get()) }
    factory<PlayerRepository> { PlayerRepositoryImpl(get()) }

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
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
}
val app = module {
    viewModel { FavoritesViewModel() }
    viewModel { PlaylistViewModel() }
    viewModel { LibraryViewModel() }
}
