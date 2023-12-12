package com.example.moviesmanager.repository

import androidx.lifecycle.LiveData
import com.example.moviesmanager.data.FilmDAO
import com.example.moviesmanager.data.Film

class FilmRepository (private val filmDAO: FilmDAO) {

    suspend fun insert(film: Film){
        filmDAO.insert(film)
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