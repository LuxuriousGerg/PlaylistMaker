package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.Track
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApiService {
    @GET("search")
    suspend fun searchTracks(@Query("term") searchText: String): Track.SearchResponse
}
