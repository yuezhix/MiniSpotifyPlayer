# MiniSpotifyPlayer

MiniSpotifyPlayer is an Android music player app built with Kotlin and Jetpack Compose. Users browse album sections on the home page, open an album to see its songs, play music from a mini player bar, and save favorite albums on the device.

The app follows the MVVM pattern with Hilt for dependency injection. Album and song data come from a Ktor backend in `server/`, which returns JSON from local files and serves the MP3 files. Favorites are stored in a Room database, and playback uses ExoPlayer.

## Features

- Home page with album sections loaded from `GET /feed`, shown as horizontal rows of album covers
- Album page with a vinyl-style cover that rotates while music plays, album information, and the song list
- Tap a song to stream it with ExoPlayer; the song that is playing is shown in green
- Mini player bar above the bottom navigation: cover, song name, play and pause, seek bar, and a close button that stops playback and hides the bar
- Favorite button on the album page; favorites are saved in Room and shown on the Favorite tab, which updates automatically
- Bottom navigation between Home and Favorite with Jetpack Navigation; Safe Args passes the selected album to the album page
- Network errors are caught in the ViewModels, so the app keeps running when the backend is not available

## Tech Stack

| Area | Technology |
| --- | --- |
| Language | Kotlin 1.7 |
| UI | Jetpack Compose, Material, Fragments with XML host layout |
| Architecture | MVVM, ViewModel, StateFlow, Kotlin Coroutines and Flow |
| Dependency injection | Hilt 2.44 |
| Navigation | Jetpack Navigation 2.5, Safe Args |
| Network | Retrofit 2.9, Gson, OkHttp |
| Database | Room 2.4 |
| Images | Coil (`AsyncImage`) |
| Playback | ExoPlayer 2.18 |
| Backend | Ktor 2.2 (Netty), kotlinx.serialization |

## Architecture

```
MainActivity (Hilt entry point)
  ├── NavHostFragment                   Home, Favorite, Playlist fragments (Compose screens)
  │     └── ViewModels (StateFlow)
  │           ├── HomeRepository, PlaylistRepository ── Retrofit ──→ Ktor server (:8080)
  │           └── FavoriteAlbumRepository ────────────────────────→ Room (Album table)
  └── PlayerBar (Compose) ── PlayerViewModel ── ExoPlayer ──→ /songs/*.mp3
```

Each screen collects a `StateFlow` from its ViewModel, and each ViewModel reads data through a repository. Hilt modules provide `Retrofit`, `NetworkApi`, `AppDatabase`, and `ExoPlayer` as singletons. `MainActivity` creates `PlayerViewModel`, and `PlaylistFragment` gets the same instance with `activityViewModels()`, so a song tapped on the album page also updates the player bar.

## Project Structure

Android paths are under `app/src/main/java/com/laioffer/minispotifyplayer/`.

| Path | Responsibility |
| --- | --- |
| `MainApplication.kt` | Application class with `@HiltAndroidApp`. |
| `MainActivity.kt` | Sets up the bottom navigation and the player bar. |
| `network/` | Retrofit API (`NetworkApi`) and the Hilt module with the backend URL. |
| `database/` | Room database, `DatabaseDao`, and the Hilt module. |
| `datamodel/` | `Album`, `Section`, `Playlist`, and `Song` data classes. |
| `repository/` | Repositories for the home feed, playlists, and favorite albums. |
| `ui/home/` | Home fragment, screen, and ViewModel. |
| `ui/playlist/` | Album page: cover, song list, and favorite button. |
| `ui/favorite/` | Favorite album list. |
| `player/` | ExoPlayer Hilt module, `PlayerViewModel`, and `PlayerBar`. |
| `app/src/main/res/navigation/nav_graph.xml` | Navigation graph and the `Album` argument of the album page. |
| `server/src/main/kotlin/.../Application.kt` | Ktor server: routes and static song files on port 8080. |
| `server/src/main/resources/` | `feed.json`, `playlists.json`, and the `static/songs/` folder. |

## API

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/feed` | Album sections for the home page |
| GET | `/playlist/{id}` | Songs of one album |
| GET | `/playlists` | All playlists |
| GET | `/songs/{file}` | MP3 file of a song |

Each song in `playlists.json` has `name`, `lyric`, `src`, and `length`. The `src` field is the full URL of the MP3 file.

## Run Locally

Requirements:

- Android Studio with an Android emulator (API 23 or later)
- JDK 11 for the backend

### Backend

Song files are not included in the repository. Put your MP3 files in `server/src/main/resources/static/songs/`, with the file names used in `playlists.json`.

Start the server:

```bash
cd server
./gradlew run
```

Open http://localhost:8080/feed to make sure that the server returns JSON.

### Android app

Open the project root in Android Studio, then run the `app` configuration on an emulator.

The emulator reaches the backend on your computer through `10.0.2.2`. To use a physical device, change the backend URL in `network/NetworkModule.kt`, the allowed domain in `network_security_config.xml`, and the song URLs in `playlists.json`.

## Configuration

| Name | Where | Description |
| --- | --- | --- |
| `BASE_URL` | `network/NetworkModule.kt` | Backend URL, default `http://10.0.2.2:8080/` |
| Cleartext domain | `app/src/main/res/xml/network_security_config.xml` | HTTP is allowed only for `10.0.2.2` |
| Port | `server/.../Application.kt` | Backend port, default `8080` |
| Song URLs | `server/src/main/resources/playlists.json` | Full URL of each MP3 file |

`local.properties`, build output, IDE files, and the MP3 files in `server/src/main/resources/static/songs/` are ignored by Git.
