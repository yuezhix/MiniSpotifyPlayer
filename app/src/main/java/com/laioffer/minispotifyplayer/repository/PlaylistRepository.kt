package com.laioffer.minispotifyplayer.repository

import com.laioffer.minispotifyplayer.datamodel.Playlist
import com.laioffer.minispotifyplayer.network.NetworkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistRepository @Inject constructor(private val networkApi: NetworkApi) {

    suspend fun getPlaylist(id: Int): Playlist = withContext(Dispatchers.IO) {
        networkApi.getPlaylist(id).execute().body()!!
    }
}