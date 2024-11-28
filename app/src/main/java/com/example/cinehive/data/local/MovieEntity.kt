package com.example.cinehive.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val posterPath: String,
    val isFavorite: Boolean = false,
    val isWatched: Boolean = false,
    val rating: Int? = null,  // Rating from 1-5, null if not rated
    val addedDate: Long = System.currentTimeMillis()
)