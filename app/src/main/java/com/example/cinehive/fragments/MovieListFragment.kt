package com.example.cinehive.fragments

import androidx.fragment.app.Fragment
import com.example.cinehive.databinding.MovieListFragmentBinding

class MovieListFragment : Fragment() {
    private lateinit var binding: MovieListFragmentBinding
    private val viewModel: MovieListViewModel by viewModels()
}