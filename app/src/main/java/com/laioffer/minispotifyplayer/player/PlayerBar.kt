package com.laioffer.minispotifyplayer.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.laioffer.minispotifyplayer.R
import com.laioffer.minispotifyplayer.ui.theme.TransparentBlack

@Composable
fun PlayerBar(viewModel: PlayerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isVisible = uiState.album != null && uiState.song != null

    AnimatedVisibility(isVisible) {
        PlayerBarContent(uiState = uiState,
            togglePlay = {
                if (uiState.isPlaying) {
                    viewModel.pause()
                } else {
                    viewModel.play()
                }
            },
            seekTo = {
                viewModel.seekTo(it)
            },
            onClose = {
                viewModel.clear()
            }
        )
    }
}

@Composable
private fun PlayerBarContent(
    uiState: PlayerUiState,
    togglePlay: () -> Unit,
    seekTo: (Long) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(TransparentBlack)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 32.dp, top = 8.dp, bottom = 8.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = uiState.album?.cover,
                    contentDescription = null,
                    modifier = Modifier
                        .width(60.dp)
                        .aspectRatio(1.0f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.FillBounds
                )

                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = uiState.song?.name ?: "",
                        style = MaterialTheme.typography.body2,
                        color = Color.White,
                    )
                    Text(
                        text = uiState.song?.lyric ?: "",
                        style = MaterialTheme.typography.caption,
                        color = Color.White,
                    )
                }

                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            togglePlay()
                        },
                    painter = painterResource(
                        id = if (uiState.isPlaying) {
                            R.drawable.ic_pause_24
                        } else {
                            R.drawable.ic_play_arrow_24
                        }
                    ),
                    tint = Color.White,
                    contentDescription = ""
                )
            }

            // x out button: clear the current song
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .size(20.dp)
                    .clickable {
                        onClose()
                    },
                painter = painterResource(id = R.drawable.ic_close_24),
                tint = Color.LightGray,
                contentDescription = "Clear current song"
            )
        }

        SeekBar(
            uiState.currentMs.toFloat(),
            uiState.durationMs.toFloat()
        ) {
            seekTo(it)
        }
    }
}

@Composable
private fun SeekBar(currentMs: Float, durationValue: Float, seekTo: (Long) -> Unit) {
    var seekBarPosition by remember { mutableStateOf(0f) }
    var seeking by remember { mutableStateOf(false) }
    if (!seeking) {
        seekBarPosition = currentMs
    }
    Slider(
        modifier = Modifier.height(24.dp),
        value = seekBarPosition,
        valueRange = 0f..durationValue,
        onValueChange = {
            seeking = true
            seekBarPosition = it

        },
        onValueChangeFinished = {
            seekTo(seekBarPosition.toLong())
            seeking = false
        },
        colors = SliderDefaults.colors(
            thumbColor = Color.Transparent,
            inactiveTrackColor = Color.LightGray,
            activeTrackColor = Color.Green
        )
    )
}

