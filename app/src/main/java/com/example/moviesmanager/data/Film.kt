package com.example.moviesmanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)])
data class Film (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "name")
    var name: String,
    var releaseYear: String,
    var studio: String,
    var duration: Int,
    var isBeenWatched: Boolean,
    var score: Int,
    var gender: String,
)
