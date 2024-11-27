package com.example.cinehive

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinehive.adapters.MovieAdapter
import com.example.cinehive.viewmodels.MovieViewModel

class MoviesFragment : Fragment() {

    private val movieViewModel: MovieViewModel by viewModels()

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movies, container, false)

        recyclerView = view.findViewById(R.id.movie_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        movieAdapter = MovieAdapter(mutableListOf())
        recyclerView.adapter = movieAdapter

        movieViewModel.movies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.updateMovies(movies)
        }

        movieViewModel.getPopularMovies(BuildConfig.TMDB_API_KEY)

        return view
    }
}