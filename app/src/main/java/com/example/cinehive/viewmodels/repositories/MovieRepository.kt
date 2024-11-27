package com.example.cinehive.viewmodels.repositories

import com.example.cinehive.api.TMDBService
import com.example.cinehive.dataclasses.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MovieRepository(private val apiService: TMDBService) {

    suspend fun fetchPopularMovies(apiKey: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPopularMovies(apiKey)
                if (response.isSuccessful) {
                    response.body()?.results ?: emptyList()
                } else {
                    throw Exception("Failed to fetch movies: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}