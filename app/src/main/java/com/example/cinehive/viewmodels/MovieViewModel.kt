package com.example.cinehive.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cinehive.viewmodels.repositories.MovieRepository
import androidx.lifecycle.ViewModel
import com.example.cinehive.api.RetrofitInstance
import androidx.lifecycle.viewModelScope
import com.example.cinehive.BuildConfig
import com.example.cinehive.dataclasses.Movie
import kotlinx.coroutines.launch

class MovieViewModel() : ViewModel() {

    private val repository = MovieRepository(RetrofitInstance.api)

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies

    fun getPopularMovies(apiKey: String) {
        viewModelScope.launch {
           val movieList = repository.fetchPopularMovies(apiKey)
            _movies.postValue(movieList)
        }
    }
}