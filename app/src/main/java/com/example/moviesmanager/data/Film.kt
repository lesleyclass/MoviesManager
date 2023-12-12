package com.example.moviesmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Film (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    var releaseYear: String,
    var isBeenWatched: Boolean
)
