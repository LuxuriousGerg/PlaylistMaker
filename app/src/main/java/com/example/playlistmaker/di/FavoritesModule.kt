package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.repository.FavoritesRepositoryImpl
import com.example.playlistmaker.domain.interactors.FavoritesInteractor
import com.example.playlistmaker.domain.repository.FavoritesRepository
import com.example.playlistmaker.presentation.viewmodel.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favoritesModule = module {
    // База данных
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "playlist_db")
            .fallbackToDestructiveMigration()
            .build()
    }
    // DAO
    single { get<AppDatabase>().favoriteTrackDao() }

    // Репозиторий
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }

    // Интерактор
    single { FavoritesInteractor(get()) }

    // Вьюмодель
    viewModel { FavoritesViewModel(get()) }
}
