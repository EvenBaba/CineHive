package com.example.cinehive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinehive.api.RetrofitInstance
import com.example.cinehive.dataclasses.Movie
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _trendingMovies = MutableLiveData<List<Movie>>()
    val trendingMovies: LiveData<List<Movie>> = _trendingMovies

    private val _topRatedMovies = MutableLiveData<List<Movie>>()
    val topRatedMovies: LiveData<List<Movie>> = _topRatedMovies

    fun getPopularMovies(apiKey: String, page: Int = 1) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getPopularMovies(apiKey, page)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    _movies.value = movieResponse.results
                }
            }
        }
    }

    fun getTrendingMovies(apiKey: String) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getTrendingMovies(apiKey)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    _trendingMovies.value = movieResponse.results
                }
            }
        }
    }

    fun getTopRatedMovies(apiKey: String) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getTopRatedMovies(apiKey)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    _topRatedMovies.value = movieResponse.results
                }
            }
        }
    }
}