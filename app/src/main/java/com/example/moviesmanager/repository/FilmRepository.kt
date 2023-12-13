package com.example.moviesmanager.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.moviesmanager.data.FilmDAO
import com.example.moviesmanager.data.Film
import com.google.android.material.snackbar.Snackbar

class FilmRepository (private val filmDAO: FilmDAO) {

    suspend fun insert(film: Film): Boolean {
        return try {
            filmDAO.insert(film)
            true
        } catch  (exception: SQLiteConstraintException){
            Log.d("TEST", "Esse filme já existe na sua lista")
            false
        }
    }

    suspend fun update(film: Film){
        filmDAO.update(film)
    }

    suspend fun delete(film: Film){
        filmDAO.delete(film)
    }

    fun getAllContacts(): LiveData<List<Film>> {
        return filmDAO.getAllFilms()
    }

    fun getFilmById(id: Int): LiveData<Film>{
        return filmDAO.getFilmById(id)
    }
}