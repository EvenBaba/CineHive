package com.example.cinehive.api

import com.example.cinehive.api.responses.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ) : Response<MovieResponse>
}