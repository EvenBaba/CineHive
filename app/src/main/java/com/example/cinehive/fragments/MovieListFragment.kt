package com.example.cinehive.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinehive.adapters.MovieListAdapter
import com.example.cinehive.databinding.MovieListFragmentBinding
import com.example.cinehive.viewmodels.MovieListViewModel

class MovieListFragment : Fragment() {
    private lateinit var binding: MovieListFragmentBinding
    private val viewModel: MovieListViewModel by viewModels()
    private lateinit var adapter: MovieListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MovieListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MovieListAdapter()
        binding.rvMovieList.adapter = adapter
        binding.rvMovieList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.liveMovies.observe(viewLifecycleOwner) { movieList ->
            adapter.updateMovies(movieList)
        }

        viewModel.loadMovies()
    }
}