package com.example.cinehive.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cinehive.R
import com.example.cinehive.data.local.MovieEntity
import com.example.cinehive.dataclasses.Movie

class LibraryMovieAdapter(
    private val onFavoriteClick: (Int) -> Unit,
    private val onWatchedClick: (Int) -> Unit,
    private val onRatingChanged: (Int, Int) -> Unit,
    private val onMovieClick: (Movie) -> Unit
) : ListAdapter<MovieEntity, LibraryMovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val posterImageView: ImageView = view.findViewById(R.id.movie_poster)
        val titleTextView: TextView = view.findViewById(R.id.movie_title)
        val favoriteButton: ImageButton = view.findViewById(R.id.favorite_button)
        val watchedButton: ImageButton = view.findViewById(R.id.watched_button)
        val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.library_movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movieEntity = getItem(position)

        holder.titleTextView.text = movieEntity.title
        holder.posterImageView.load("https://image.tmdb.org/t/p/w500${movieEntity.posterPath}")

        // Set favorite button state
        holder.favoriteButton.setImageResource(
            if (movieEntity.isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )

        // Set watched button state
        holder.watchedButton.setImageResource(
            if (movieEntity.isWatched) R.drawable.ic_watched
            else R.drawable.ic_unwatched
        )

        // Set rating
        holder.ratingBar.rating = movieEntity.rating?.toFloat() ?: 0f

        // Click listeners
        holder.favoriteButton.setOnClickListener {
            onFavoriteClick(movieEntity.id)
        }

        holder.watchedButton.setOnClickListener {
            onWatchedClick(movieEntity.id)
        }

        holder.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                onRatingChanged(movieEntity.id, rating.toInt())
            }
        }

        // Add click listener for the entire item
        holder.itemView.setOnClickListener {
            // Convert MovieEntity to Movie with all available data
            val movie = Movie(
                id = movieEntity.id,
                title = movieEntity.title,
                poster_path = movieEntity.posterPath,
                backdrop_path = movieEntity.backdropPath,
                overview = movieEntity.overview,
                release_date = movieEntity.releaseDate,
                vote_average = movieEntity.voteAverage,
                vote_count = movieEntity.voteCount,
                // Default values for non-stored fields
                adult = false,
                genre_ids = listOf(),
                original_language = "en",
                original_title = movieEntity.title,
                popularity = 0.0,
                video = false
            )
            onMovieClick(movie)
        }
    }
}

private class MovieDiffCallback : DiffUtil.ItemCallback<MovieEntity>() {
    override fun areItemsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
        return oldItem == newItem
    }
}