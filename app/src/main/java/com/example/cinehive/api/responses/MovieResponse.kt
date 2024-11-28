package com.example.cinehive.api.responses

import com.example.cinehive.dataclasses.Movie

data class MovieResponse(
    val page: Int,
    val total_pages: Int,
    val results: List<Movie>
)
