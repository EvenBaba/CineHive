package com.example.cinehive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.cinehive.databinding.FragmentMovieDetailBinding
import com.example.cinehive.dataclasses.Movie

class MovieDetailFragment : Fragment() {
    private lateinit var movie: Movie

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMovieDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_movie_detail, container, false
        )

        movie = MovieDetailFragmentArgs.fromBundle(requireArguments()).movie
        binding.movie = movie // Set the movie object to be bound

        return binding.root
    }
}