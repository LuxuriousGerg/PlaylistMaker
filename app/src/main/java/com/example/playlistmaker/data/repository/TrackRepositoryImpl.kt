package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.TrackDTO
import com.example.playlistmaker.data.network.iTunesApiService
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException

class TrackRepositoryImpl(private val apiService: iTunesApiService) : TrackRepository {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val response = apiService.searchTracks(query)
            if (response.isSuccessful && response.body() != null) {
                val tracks = response.body()?.results?.mapNotNull { mapToDomain(it) } ?: emptyList()
                emit(Result.success(tracks))
            } else {
                emit(Result.failure(IOException("Error: ${response.code()}")))
            }
        } catch (e: HttpException) {
            emit(Result.failure(IOException("Network error", e)))
        } catch (e: IOException) {
            emit(Result.failure(IOException("Check your internet connection", e)))
        }
    }.flowOn(Dispatchers.IO)


    private fun mapToDomain(dto: TrackDTO): Track {
        return Track(
            trackName = dto.trackName ?: "Unknown Track",
            artistName = dto.artistName ?: "Unknown Artist",
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100,
            collectionName = dto.collectionName ?: "Unknown Collection",
            releaseDate = dto.releaseDate ?: "Unknown Date",
            primaryGenreName = dto.primaryGenreName ?: "Unknown Genre",
            country = dto.country ?: "Unknown Country",
            previewUrl = dto.previewUrl ?: ""
        )
    }
}