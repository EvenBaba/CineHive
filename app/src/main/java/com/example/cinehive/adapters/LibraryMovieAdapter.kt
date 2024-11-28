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

class LibraryMovieAdapter(
    private val onFavoriteClick: (Int) -> Unit,
    private val onWatchedClick: (Int) -> Unit,
    private val onRatingChanged: (Int, Int) -> Unit
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
        val movie = getItem(position)

        holder.titleTextView.text = movie.title
        holder.posterImageView.load("https://image.tmdb.org/t/p/w500${movie.posterPath}")

        // Set favorite button state
        holder.favoriteButton.setImageResource(
            if (movie.isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )

        // Set watched button state
        holder.watchedButton.setImageResource(
            if (movie.isWatched) R.drawable.ic_watched
            else R.drawable.ic_unwatched
        )

        // Set rating
        holder.ratingBar.rating = movie.rating?.toFloat() ?: 0f

        // Click listeners
        holder.favoriteButton.setOnClickListener {
            onFavoriteClick(movie.id)
        }

        holder.watchedButton.setOnClickListener {
            onWatchedClick(movie.id)
        }

        holder.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                onRatingChanged(movie.id, rating.toInt())
            }
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