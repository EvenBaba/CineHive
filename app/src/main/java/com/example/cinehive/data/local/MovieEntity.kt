package com.example.cinehive.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cinehive.dataclasses.Movie

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val posterPath: String,
    val backdropPath: String,
    val overview: String,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val isFavorite: Boolean = false,
    val isWatched: Boolean = false,
    val rating: Int? = null,  // Rating from 1-10, null if not rated
    val addedDate: Long = System.currentTimeMillis()
)

fun MovieEntity.toMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        poster_path = this.posterPath ?: "",
        backdrop_path = this.backdropPath ?: "",
        overview = this.overview ?: "",
        release_date = this.releaseDate ?: "",
        vote_average = this.voteAverage ?: 0.0,
        vote_count = this.voteCount ?: 0,
        genre_ids = listOf(),  // Bu kısım veritabanında yoksa boş liste verilir
        adult = false,  // Varsayılan değer
        original_language = "en",  // Varsayılan değer
        original_title = this.title,
        popularity = 0.0,
        video = false
    )
}