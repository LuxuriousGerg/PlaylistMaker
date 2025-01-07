package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.network.iTunesApiService
import com.example.playlistmaker.data.repository.SearchHistory
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.interactors.*
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    // Settings Interactor
    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository(context))
    }

    private fun provideSettingsRepository(context: Context): SettingsRepository {
        val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        return SettingsRepositoryImpl(sharedPreferences)
    }

    // History Interactor
    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        return HistoryInteractorImpl(provideHistoryRepository(context))
    }

    private fun provideHistoryRepository(context: Context): HistoryRepository {
        val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        return SearchHistory(sharedPreferences)
    }

    // Search Tracks Interactor
    fun provideSearchTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(provideTrackRepository())
    }

    private fun provideTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(provideApiService())
    }

    private fun provideApiService(): iTunesApiService {
        return Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApiService::class.java)
    }

    // Player Interactor
    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl()
    }
}
