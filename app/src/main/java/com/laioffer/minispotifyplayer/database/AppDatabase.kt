package com.laioffer.minispotifyplayer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.laioffer.minispotifyplayer.datamodel.Album

@Database(entities = [Album::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao
}

