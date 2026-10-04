package com.laioffer.minispotifyplayer.network

import com.laioffer.minispotifyplayer.datamodel.Playlist
import com.laioffer.minispotifyplayer.datamodel.Section
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NetworkApi {
    @GET("feed")
    fun getHomeFeed(): Call<List<Section>>

    @GET("playlist/{id}")
    fun getPlaylist(@Path("id") id: Int): Call<Playlist>

}
