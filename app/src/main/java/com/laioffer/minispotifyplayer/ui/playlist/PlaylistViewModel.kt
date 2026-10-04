package com.laioffer.minispotifyplayer.ui.playlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laioffer.minispotifyplayer.datamodel.Album
import com.laioffer.minispotifyplayer.datamodel.Song
import com.laioffer.minispotifyplayer.repository.FavoriteAlbumRepository
import com.laioffer.minispotifyplayer.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val favoriteAlbumRepository: FavoriteAlbumRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        PlaylistUiState(
            Album.empty()
        )
    )
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    // keep references so that calling fetchPlaylist again (e.g. view recreated) cancels the old jobs
    // instead of stacking up duplicate collectors
    private var playlistJob: Job? = null
    private var favoriteJob: Job? = null

    fun fetchPlaylist(album: Album) {
        _uiState.value = _uiState.value.copy(album = album)

        playlistJob?.cancel()
        playlistJob = viewModelScope.launch {
            try {
                val playlist = playlistRepository.getPlaylist(album.id)
                _uiState.value = _uiState.value.copy(playlist = playlist.songs)
                Log.d("PlaylistViewModel", _uiState.value.toString())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("PlaylistViewModel", "fetchPlaylist failed", e)
            }
        }

        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            favoriteAlbumRepository.isFavoriteAlbum(album.id).collect{
                _uiState.value = _uiState.value.copy(
                    isFavorite = it
                )
            }
        }
    }

    fun toggleFavorite(isFavorite: Boolean) {
        val album = _uiState.value.album
        viewModelScope.launch {
            if (isFavorite) {
                favoriteAlbumRepository.favoriteAlbum(album)
            } else {
                favoriteAlbumRepository.unFavoriteAlbum(album)
            }
        }
    }

}

data class PlaylistUiState(
    val album: Album,
    val isFavorite: Boolean = false,
    val playlist: List<Song> = emptyList()
)

