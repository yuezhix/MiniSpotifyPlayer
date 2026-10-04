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

fun Application.module() { // Extension
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }

    // TODO: adding the routing configuration here
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/feed") {
            val jsonString: String? = this::class.java.classLoader.getResource("feed.json")?.readText()
            call.respondText(jsonString ?: "", contentType = ContentType.Application.Json)

//            if (jsonString != null) {
//                call.respondText(jsonString, contentType = ContentType.Application.Json)
//            } else {
//                call.respondText("", contentType = ContentType.Application.Json)
//            }
        }

        get("/playlists") {
            val jsonString: String? = this::class.java.classLoader.getResource("playlists.json")?.readText()
            call.respondText(jsonString ?: "", contentType = ContentType.Application.Json)
        }

        get("playlist/{id}") {
//            val jsonString: String? = this::class.java.classLoader.getResource("playlists.json")?.readText()
            // jsonString -> List<Playlist>
            // string/bytes -> object: deserialize/decode
            // object -> string/bytes: serialize
//            if (jsonString != null) {
//                val playlists: List<Playlist> = Json.decodeFromString(ListSerializer(Playlist.serializer()), jsonString)
//                val id = call.parameters["id"]
//                val playlist = playlists.firstOrNull { item: Playlist ->  item.id.toString() == id }
//                call.respondNullable(playlist)
//            } else {
//                call.respond("null")
//            }

            // let
            this::class.java.classLoader.getResource("playlists.json")?.readText()?.let { jsonString -> // if (jsonString != null)
                val playlists: List<Playlist> = Json.decodeFromString(ListSerializer(Playlist.serializer()), jsonString)
                val id = call.parameters["id"]
                val playlist = playlists.firstOrNull { item: Playlist ->  item.id.toString() == id }
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

    myRouting {
        println("hello world")

        myGet("/") {

        }
    }
}

fun myRouting(block: () -> Unit) {
    block()
}

fun myGet(path: String, block: () -> Unit) {

}

