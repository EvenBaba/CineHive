package com.example.cinehive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cinehive.viewmodels.repositories.MovieRepository
import androidx.lifecycle.ViewModel
import com.example.cinehive.api.RetrofitInstance
import androidx.lifecycle.viewModelScope
import com.example.cinehive.dataclasses.Movie
import kotlinx.coroutines.launch

class MovieViewModel() : ViewModel() {

    private val repository = MovieRepository(RetrofitInstance.api)

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies

    private var allMovies = mutableListOf<Movie>()

    fun getPopularMovies(apiKey: String, page: Int) {
        viewModelScope.launch {
           val movieList = repository.fetchPopularMovies(apiKey, page)
            allMovies.addAll(movieList)
            _movies.value = allMovies
        }
    }
}