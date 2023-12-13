package com.example.moviesmanager.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.jetbrains.annotations.NotNull

@Dao
interface FilmDAO {
    @Insert(entity = Film::class, onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(film: Film)

    @Update
    suspend fun update (film: Film)

    @Delete
    suspend fun delete(film: Film)

    @Query("SELECT * FROM film ORDER BY name")
    fun getAllFilms(): LiveData<List<Film>>

    @Query("SELECT * FROM film WHERE id=:id")
    fun getFilmById(id: Int): LiveData<Film>
}
