package com.laioffer.minispotifyplayer.repository

import com.laioffer.minispotifyplayer.datamodel.Section
import com.laioffer.minispotifyplayer.network.NetworkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeRepository @Inject constructor(private val networkApi: NetworkApi) {

    suspend fun getHomeSections(): List<Section> = withContext(Dispatchers.IO) {
        networkApi.getHomeFeed().execute().body() ?: emptyList()
    }
}