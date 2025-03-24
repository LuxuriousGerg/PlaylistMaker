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
import com.example.playlistmaker.presentation.viewmodel.LibraryViewModel
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

    // Retrofit
    single<iTunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApiService::class.java)
    }

    // Репозитории
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<HistoryRepository> { SearchHistory(get(), get()) }
    factory<PlayerRepository> { PlayerRepositoryImpl(get()) }

    // SharedPreferences
    single {
        androidContext().getSharedPreferences("playlist_prefs", android.content.Context.MODE_PRIVATE)
    }

    // Инициализация базы данных Room
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // DAO для плейлистов
    single { get<AppDatabase>().playlistDao() }

    // DAO для треков в плейлистах (добавляемое)
    single { get<AppDatabase>().playlistTrackDao() }

    // Репозиторий для плейлистов — теперь принимает 2 DAO
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

    // PlaylistInteractor требует Context и PlaylistRepository
    factory { PlaylistInteractor(androidContext(), get()) }
}

val viewModelModule: Module = module {
    viewModel { PlayerViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { PlaylistViewModel(get()) }
}

// Модуль для экрана «Медиатека»
val libraryModule = module {
    viewModel { LibraryViewModel() }
}

// Модуль для экрана «Создание плейлиста»
val createPlaylistModule = module {
    viewModel {
        CreatePlaylistViewModel(
            playlistInteractor = get()
        )
    }
}
