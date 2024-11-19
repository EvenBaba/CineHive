package com.example.cinehive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cinehive.dataclasses.Movie

class MovieListViewModel : ViewModel() {
    private val mutableLiveMovies = MutableLiveData<List<Movie>>()
    val liveMovies: LiveData<List<Movie>> = mutableLiveMovies

    fun loadMovies() {
        val movieList = mutableListOf<Movie>()

        for (i in 0..100) {
            movieList.add(Movie("movie $i"))
        }

        mutableLiveMovies.value = movieList
    }
}