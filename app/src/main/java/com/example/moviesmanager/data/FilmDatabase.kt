package com.example.moviesmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moviesmanager.constants.Constants.FILM_LIST_DATABASE_NAME

@Database(entities = [Film::class], version = 2)
abstract class FilmDatabase: RoomDatabase() {
    abstract fun filmDAO(): FilmDAO

    companion object {
        @Volatile
        private var INSTANCE: FilmDatabase? = null

        fun getDatabase(context: Context): FilmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        FilmDatabase::class.java,
                        FILM_LIST_DATABASE_NAME,
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
