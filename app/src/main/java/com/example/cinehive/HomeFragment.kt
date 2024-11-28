package com.example.cinehive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinehive.adapters.HomeMovieAdapter
import com.example.cinehive.dataclasses.Movie
import com.example.cinehive.viewmodels.MovieViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private val movieViewModel: MovieViewModel by viewModels()
    private var _searchEditText: TextInputEditText? = null
    private var _searchRecyclerView: RecyclerView? = null
    private var _trendingRecyclerView: RecyclerView? = null
    private var _topRatedRecyclerView: RecyclerView? = null

    private var searchAdapter: HomeMovieAdapter? = null
    private var trendingAdapter: HomeMovieAdapter? = null
    private var topRatedAdapter: HomeMovieAdapter? = null

    // Safe access to views
    private val searchEditText get() = _searchEditText!!
    private val searchRecyclerView get() = _searchRecyclerView!!
    private val trendingRecyclerView get() = _trendingRecyclerView!!
    private val topRatedRecyclerView get() = _topRatedRecyclerView!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupSearch()
        observeViewModelData()
        loadMovieData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up references
        _searchEditText = null
        _searchRecyclerView = null
        _trendingRecyclerView = null
        _topRatedRecyclerView = null
        searchAdapter = null
        trendingAdapter = null
        topRatedAdapter = null
    }

    private fun setupViews(view: View) {
        _searchEditText = view.findViewById(R.id.search_edit_text)
        _searchRecyclerView = view.findViewById(R.id.search_results_recycler_view)
        _trendingRecyclerView = view.findViewById(R.id.trending_movies_recycler_view)
        _topRatedRecyclerView = view.findViewById(R.id.top_rated_movies_recycler_view)

        // Setup search results
        searchRecyclerView.layoutManager = LinearLayoutManager(context)
        searchAdapter = HomeMovieAdapter(
            mutableListOf(),
            onFavoriteClick = { movie -> handleFavoriteClick(movie) },
            onWatchedClick = { movie -> handleWatchedClick(movie) }
        )
        searchRecyclerView.adapter = searchAdapter

        // Setup trending movies
        trendingRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        trendingAdapter = HomeMovieAdapter(
            mutableListOf(),
            onFavoriteClick = { movie -> handleFavoriteClick(movie) },
            onWatchedClick = { movie -> handleWatchedClick(movie) }
        )
        trendingRecyclerView.adapter = trendingAdapter

        // Setup top rated movies
        topRatedRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        topRatedAdapter = HomeMovieAdapter(
            mutableListOf(),
            onFavoriteClick = { movie -> handleFavoriteClick(movie) },
            onWatchedClick = { movie -> handleWatchedClick(movie) }
        )
        topRatedRecyclerView.adapter = topRatedAdapter
    }

    private fun setupSearch() {
        searchEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) {
                    movieViewModel.searchMovies(BuildConfig.TMDB_API_KEY, query)
                    searchRecyclerView.isVisible = true
                }
                true
            } else {
                false
            }
        }
    }

    private fun observeViewModelData() {
        movieViewModel.trendingMovies.observe(viewLifecycleOwner) { movies ->
            trendingAdapter?.updateMovies(movies)
            updateLibraryStates(movies, trendingAdapter)
        }

        movieViewModel.topRatedMovies.observe(viewLifecycleOwner) { movies ->
            topRatedAdapter?.updateMovies(movies)
            updateLibraryStates(movies, topRatedAdapter)
        }

        movieViewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            searchAdapter?.updateMovies(movies)
            updateLibraryStates(movies, searchAdapter)
        }
    }

    private fun updateLibraryStates(movies: List<Movie>, adapter: HomeMovieAdapter?) {
        adapter ?: return
        movies.forEach { movie ->
            lifecycleScope.launch {
                val libraryState = movieViewModel.isMovieInLibrary(movie.id)
                adapter.updateMovieState(movie.id, libraryState)
            }
        }
    }

    private fun loadMovieData() {
        movieViewModel.getTrendingMovies(BuildConfig.TMDB_API_KEY)
        movieViewModel.getTopRatedMovies(BuildConfig.TMDB_API_KEY)
    }

    private fun handleFavoriteClick(movie: Movie) {
        lifecycleScope.launch {
            val existingMovie = movieViewModel.isMovieInLibrary(movie.id)
            if (existingMovie == null) {
                movieViewModel.addToLibrary(movie, isFavorite = true)
            } else {
                movieViewModel.addToLibrary(movie, isFavorite = !existingMovie.isFavorite,
                    isWatched = existingMovie.isWatched)
            }
        }
    }

    private fun handleWatchedClick(movie: Movie) {
        lifecycleScope.launch {
            val existingMovie = movieViewModel.isMovieInLibrary(movie.id)
            if (existingMovie == null) {
                movieViewModel.addToLibrary(movie, isWatched = true)
            } else {
                movieViewModel.addToLibrary(movie, isFavorite = existingMovie.isFavorite,
                    isWatched = !existingMovie.isWatched)
            }
        }
    }
}