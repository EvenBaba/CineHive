package com.example.cinehive.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cinehive.api.RetrofitInstance
import com.example.cinehive.api.responses.MovieResponse
import com.example.cinehive.data.local.AppDatabase
import com.example.cinehive.data.local.MovieEntity
import com.example.cinehive.data.repository.LibraryRepository
import com.example.cinehive.dataclasses.Movie
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val libraryRepository: LibraryRepository

    private val _popularMovies = MutableLiveData<MovieResponse>()
    val popularMovies: LiveData<MovieResponse> = _popularMovies

    private val _trendingMovies = MutableLiveData<MovieResponse>()
    val trendingMovies: LiveData<MovieResponse> = _trendingMovies

    private val _topRatedMovies = MutableLiveData<MovieResponse>()
    val topRatedMovies: LiveData<MovieResponse> = _topRatedMovies

    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    init {
        val movieDao = AppDatabase.getDatabase(application).movieDao()
        libraryRepository = LibraryRepository(movieDao)
    }

    fun getPopularMovies(apiKey: String, page: Int = 1) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getPopularMovies(apiKey, page)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    _popularMovies.value = movieResponse
                }
            }
        }
    }

    fun getTrendingMovies(apiKey: String, page: Int = 1) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getTrendingMovies(apiKey, page)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    _trendingMovies.value = movieResponse
                }
            }
        }
    }

    fun getTopRatedMovies(apiKey: String, page: Int = 1) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getTopRatedMovies(apiKey, page)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    _topRatedMovies.value = movieResponse
                }
            }
        }
    }

    fun searchMovies(apiKey: String, query: String) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.searchMovies(apiKey, query)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    _searchResults.value = movieResponse.results
                }
            }
        }
    }

    fun addToLibrary(
        movie: Movie,
        isFavorite: Boolean = false,
        isWatched: Boolean = false,
        rating: Int? = null
    ) = viewModelScope.launch {
        libraryRepository.addToLibrary(movie, isFavorite, isWatched, rating)
    }

    fun removeFromLibrary(movieId: Int) = viewModelScope.launch {
        libraryRepository.removeFromLibrary(movieId)
    }

    suspend fun isMovieInLibrary(movieId: Int): MovieEntity? {
        return libraryRepository.getMovieById(movieId)
    }
}