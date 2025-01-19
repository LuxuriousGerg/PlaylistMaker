package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.models.TrackDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApiService {
    @GET("search")
    suspend fun searchTracks(@Query("term") searchText: String): Response<TrackResponse>
}

data class TrackResponse(
    val resultCount: Int,
    val results: List<TrackDTO>
)