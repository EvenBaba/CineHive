package com.example.cinehive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cinehive.databinding.FragmentMovieDetailBinding
import com.example.cinehive.dataclasses.Movie
import com.example.cinehive.data.local.MovieEntity
import com.example.cinehive.viewmodels.MovieViewModel
import kotlinx.coroutines.launch

class MovieDetailFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailBinding
    private lateinit var movie: Movie
    private val movieViewModel: MovieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_movie_detail, container, false
        )

        movie = MovieDetailFragmentArgs.fromBundle(requireArguments()).movie
        binding.movie = movie

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupButtons()
        setupRating()
        loadMovieState()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.title = movie.title
    }

    private fun setupButtons() {
        binding.favoriteButton.setOnClickListener {
            lifecycleScope.launch {
                val existingMovie = movieViewModel.isMovieInLibrary(movie.id)
                if (existingMovie == null) {
                    movieViewModel.addToLibrary(movie, isFavorite = true)
                    updateButtonStates(MovieEntity(
                        id = movie.id,
                        title = movie.title,
                        posterPath = movie.poster_path,
                        backdropPath = movie.backdrop_path,
                        overview = movie.overview,
                        releaseDate = movie.release_date,
                        voteAverage = movie.vote_average,
                        voteCount = movie.vote_count,
                        isFavorite = true
                    ))
                } else {
                    if (!existingMovie.isWatched && existingMovie.rating == null) {
                        movieViewModel.removeFromLibrary(movie.id)
                        updateButtonStates(existingMovie.copy(isFavorite = false))
                    } else {
                        val updatedMovie = existingMovie.copy(isFavorite = !existingMovie.isFavorite)
                        movieViewModel.addToLibrary(
                            movie,
                            isFavorite = !existingMovie.isFavorite,
                            isWatched = existingMovie.isWatched,
                            rating = existingMovie.rating
                        )
                        updateButtonStates(updatedMovie)
                    }
                }
            }
        }

        binding.watchedButton.setOnClickListener {
            lifecycleScope.launch {
                val existingMovie = movieViewModel.isMovieInLibrary(movie.id)
                if (existingMovie == null) {
                    movieViewModel.addToLibrary(movie, isWatched = true)
                    updateButtonStates(MovieEntity(
                        id = movie.id,
                        title = movie.title,
                        posterPath = movie.poster_path,
                        backdropPath = movie.backdrop_path,
                        overview = movie.overview,
                        releaseDate = movie.release_date,
                        voteAverage = movie.vote_average,
                        voteCount = movie.vote_count,
                        isWatched = true
                    ))
                } else {
                    if (!existingMovie.isFavorite && existingMovie.rating == null) {
                        movieViewModel.removeFromLibrary(movie.id)
                        updateButtonStates(existingMovie.copy(isWatched = false))
                    } else {
                        val updatedMovie = existingMovie.copy(isWatched = !existingMovie.isWatched)
                        movieViewModel.addToLibrary(
                            movie,
                            isFavorite = existingMovie.isFavorite,
                            isWatched = !existingMovie.isWatched,
                            rating = existingMovie.rating
                        )
                        updateButtonStates(updatedMovie)
                    }
                }
            }
        }
    }

    private fun setupRating() {
        binding.userRating.setOnRatingBarChangeListener { _: RatingBar, rating: Float, fromUser: Boolean ->
            if (fromUser) {
                lifecycleScope.launch {
                    val existingMovie = movieViewModel.isMovieInLibrary(movie.id)
                    if (existingMovie == null) {
                        movieViewModel.addToLibrary(
                            movie,
                            rating = rating.toInt()
                        )
                    } else {
                        movieViewModel.addToLibrary(
                            movie,
                            isFavorite = existingMovie.isFavorite,
                            isWatched = existingMovie.isWatched,
                            rating = rating.toInt()
                        )
                    }
                    updateRemoveRatingButtonVisibility(rating.toInt())
                }
            }
        }

        binding.removeRatingButton.setOnClickListener {
            lifecycleScope.launch {
                val existingMovie = movieViewModel.isMovieInLibrary(movie.id)
                if (existingMovie != null) {
                    if (!existingMovie.isFavorite && !existingMovie.isWatched) {
                        movieViewModel.removeFromLibrary(movie.id)
                        updateButtonStates(existingMovie.copy(rating = null))
                    } else {
                        movieViewModel.addToLibrary(
                            movie,
                            isFavorite = existingMovie.isFavorite,
                            isWatched = existingMovie.isWatched,
                            rating = null
                        )
                    }
                    binding.userRating.rating = 0f
                    updateRemoveRatingButtonVisibility(0)
                }
            }
        }
    }

    private fun loadMovieState() {
        lifecycleScope.launch {
            val movieState = movieViewModel.isMovieInLibrary(movie.id)
            movieState?.let {
                updateButtonStates(it)
                binding.userRating.rating = it.rating?.toFloat() ?: 0f
                updateRemoveRatingButtonVisibility(it.rating ?: 0)
            }
        }
    }

    private fun updateButtonStates(movieState: MovieEntity) {
        binding.favoriteButton.text = if (movieState.isFavorite) "Remove from Favorites" else "Add to Favorites"
        binding.favoriteButton.setIconResource(
            if (movieState.isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        )

        binding.watchedButton.text = if (movieState.isWatched) "Mark as Unwatched" else "Mark as Watched"
        binding.watchedButton.setIconResource(
            if (movieState.isWatched) R.drawable.ic_watched else R.drawable.ic_unwatched
        )
    }

    private fun updateRemoveRatingButtonVisibility(rating: Int) {
        binding.removeRatingButton.visibility = if (rating > 0) View.VISIBLE else View.GONE
    }
}