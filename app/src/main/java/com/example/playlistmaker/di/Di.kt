package com.example.playlistmaker.di

import PlayerViewModel
import SearchViewModel
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.iTunesApiService
import com.example.playlistmaker.data.repository.*
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.interactors.*
import com.example.playlistmaker.domain.repository.*
import com.example.playlistmaker.presentation.CreatePlaylistViewModel
import com.example.playlistmaker.presentation.EditPlaylistViewModel
import com.example.playlistmaker.presentation.viewmodel.PlaylistInsideViewModel
import com.example.playlistmaker.presentation.viewmodel.PlaylistViewModel
import com.example.playlistmaker.presentation.viewmodel.SettingsViewModel
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
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApiService::class.java)
    }

    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<HistoryRepository> { SearchHistory(get(), get()) }
    factory<PlayerRepository> { PlayerRepositoryImpl(get()) }

    single {
        androidContext().getSharedPreferences("playlist_prefs", android.content.Context.MODE_PRIVATE)
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().playlistDao() }

    single { get<AppDatabase>().playlistTrackDao() }

    single {
        PlaylistRepository(
            playlistDao = get(),
            playlistTrackDao = get()
        )
    }
}

val domainModule: Module = module {
    factory<SearchTracksInteractor> { SearchTracksInteractorImpl(get(), get()) }
    factory<SettingsInteractor> { SettingsInteractorImpl(get()) }
    factory<HistoryInteractor> { HistoryInteractorImpl(get()) }
    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }
    factory { PlaylistInteractor(androidContext(), get()) }
}

val viewModelModule = module {
    viewModel { PlayerViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { PlaylistViewModel(get()) }
    viewModel { PlaylistInsideViewModel(get()) }
    viewModel { EditPlaylistViewModel(get()) }
}

val createPlaylistModule = module {
    viewModel {
        CreatePlaylistViewModel(
            playlistInteractor = get()
        )
    }

    viewModel {
        EditPlaylistViewModel(
            playlistInteractor = get()
        )
    }
}