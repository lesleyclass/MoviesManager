package com.example.moviesmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.moviesmanager.data.Film
import com.example.moviesmanager.data.FilmDatabase
import com.example.moviesmanager.repository.FilmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FilmsViewModel(application: Application): AndroidViewModel(application) {
    private val repository: FilmRepository
    var allFilms : LiveData<List<Film>>
    lateinit var film : LiveData<Film>
    val genders = listOf("Ação", "Aventura", "Comédia", "Drama", "Fantasia", "Terror")
    val scores = listOf("1","2","3","4","5","6","7","8","9","10")
    init {
        val dao = FilmDatabase.getDatabase(application).filmDAO()
        repository = FilmRepository(dao)
        allFilms = repository.getAllContacts()
    }

    fun insert(film: Film): Boolean {
        var isSuccess = false
        viewModelScope.launch(Dispatchers.IO){
            isSuccess = repository.insert(film)
        }
        return isSuccess
    }

    fun update(film: Film) = viewModelScope.launch(Dispatchers.IO){
        repository.update(film)
    }

    fun delete(film: Film) = viewModelScope.launch(Dispatchers.IO){
        repository.delete(film)
    }

    fun getFilmById(id: Int) {
        viewModelScope.launch {
           film = repository.getFilmById(id)
        }
    }
}
