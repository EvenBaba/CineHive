package com.example.cinehive.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cinehive.data.local.AppDatabase
import com.example.cinehive.data.local.MovieEntity
import com.example.cinehive.data.repository.LibraryRepository
import com.example.cinehive.dataclasses.Movie
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LibraryRepository
    val allMovies: LiveData<List<MovieEntity>>
    val favoriteMovies: LiveData<List<MovieEntity>>
    val watchedMovies: LiveData<List<MovieEntity>>
    val ratedMovies: LiveData<List<MovieEntity>>

    init {
        val movieDao = AppDatabase.getDatabase(application).movieDao()
        repository = LibraryRepository(movieDao)
        allMovies = repository.allMovies
        favoriteMovies = repository.favoriteMovies
        watchedMovies = repository.watchedMovies
        ratedMovies = repository.ratedMovies
    }

    fun toggleFavorite(movieId: Int) = viewModelScope.launch {
        repository.toggleFavorite(movieId)
    }

    fun toggleWatched(movieId: Int) = viewModelScope.launch {
        repository.toggleWatched(movieId)
    }

    fun rateMovie(movieId: Int, rating: Int) = viewModelScope.launch {
        repository.rateMovie(movieId, rating)
    }

    fun removeFromLibrary(movieId: Int) = viewModelScope.launch {
        repository.removeFromLibrary(movieId)
    }

    suspend fun getMovieById(movieId: Int): MovieEntity? {
        return repository.getMovieById(movieId)
    }
}