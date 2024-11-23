package com.example.cinehive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cinehive.BuildConfig
import com.example.cinehive.dataclasses.Movie
import com.example.cinehive.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import com.example.cinehive.dataclasses.MovieResponse
import retrofit2.Response
import android.util.Log

class MovieListViewModel : ViewModel() {
    private val _liveMovies = MutableLiveData<List<Movie>>()
    val liveMovies: LiveData<List<Movie>> = _liveMovies

    fun loadMovies() {
        val apiKey = BuildConfig.TMDB_API_KEY

        RetrofitInstance.api.getMovies(apiKey, 1).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    _liveMovies.value = response.body()?.results
                } else {
                    Log.e("MovieListViewModel", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("MovieListViewModel", "Network error: ${t.localizedMessage}")
            }
        })
    }
}