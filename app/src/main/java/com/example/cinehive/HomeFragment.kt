package com.example.cinehive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinehive.adapters.HomeMovieAdapter
import com.example.cinehive.viewmodels.MovieViewModel

class HomeFragment : Fragment() {
    private val movieViewModel: MovieViewModel by viewModels()
    private lateinit var trendingRecyclerView: RecyclerView
    private lateinit var topRatedRecyclerView: RecyclerView
    private lateinit var trendingAdapter: HomeMovieAdapter
    private lateinit var topRatedAdapter: HomeMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setupRecyclerViews(view)
        observeViewModelData()
        loadMovieData()

        return view
    }

    private fun setupRecyclerViews(view: View) {
        // Trending Movies
        trendingRecyclerView = view.findViewById(R.id.trending_movies_recycler_view)
        trendingRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        trendingAdapter = HomeMovieAdapter(mutableListOf())
        trendingRecyclerView.adapter = trendingAdapter

        // Top Rated Movies
        topRatedRecyclerView = view.findViewById(R.id.top_rated_movies_recycler_view)
        topRatedRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        topRatedAdapter = HomeMovieAdapter(mutableListOf())
        topRatedRecyclerView.adapter = topRatedAdapter
    }

    private fun observeViewModelData() {
        movieViewModel.trendingMovies.observe(viewLifecycleOwner) { movies ->
            trendingAdapter.updateMovies(movies)
        }

        movieViewModel.topRatedMovies.observe(viewLifecycleOwner) { movies ->
            topRatedAdapter.updateMovies(movies)
        }
    }

    private fun loadMovieData() {
        movieViewModel.getTrendingMovies(BuildConfig.TMDB_API_KEY)
        movieViewModel.getTopRatedMovies(BuildConfig.TMDB_API_KEY)
    }
}