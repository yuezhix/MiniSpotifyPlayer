package com.laioffer.minispotifyplayer

import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticBasePackage
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
data class Playlist(
    val id: Long,
    val songs: List<Song>
)

@Serializable
data class Song (
    val name: String,
    val lyric: String,
    val src: String,
    val length: String
)

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/feed") {
            val jsonString: String? = this::class.java.classLoader.getResource("feed.json")?.readText()
            call.respondText(jsonString ?: "", contentType = ContentType.Application.Json)
        }

        get("/playlists") {
            val jsonString: String? = this::class.java.classLoader.getResource("playlists.json")?.readText()
            call.respondText(jsonString ?: "", contentType = ContentType.Application.Json)
        }

        get("playlist/{id}") {
            this::class.java.classLoader.getResource("playlists.json")?.readText()?.let { jsonString ->
                val playlists: List<Playlist> = Json.decodeFromString(ListSerializer(Playlist.serializer()), jsonString)
                val id = call.parameters["id"]
                val playlist = playlists.firstOrNull { item: Playlist -> item.id.toString() == id }
                call.respondNullable(playlist)
            } ?: call.respond("null")
        }

        static("/") {
            staticBasePackage = "static"
            static("songs") {
                resources("songs")
            }
        }
    }
}
