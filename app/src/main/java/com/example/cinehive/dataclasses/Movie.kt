package com.example.cinehive.dataclasses

data class MovieResponse(
    val results: List<Movie>
)

data class Movie(
    val name: String,
    val posterPath: String? = null
)
