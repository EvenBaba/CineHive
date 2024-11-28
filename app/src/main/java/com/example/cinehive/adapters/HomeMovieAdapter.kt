package com.example.cinehive.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cinehive.R
import com.example.cinehive.data.local.MovieEntity
import com.example.cinehive.dataclasses.Movie

class HomeMovieAdapter(
    private val movies: MutableList<Movie>,
    private val onFavoriteClick: (Movie) -> Unit,
    private val onWatchedClick: (Movie) -> Unit
) : RecyclerView.Adapter<HomeMovieAdapter.MovieViewHolder>() {

    private val movieStates = mutableMapOf<Int, MovieEntity?>()

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moviePosterImageView: ImageView = itemView.findViewById(R.id.movie_poster)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favorite_button)
        val watchedButton: ImageButton = itemView.findViewById(R.id.watched_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.moviePosterImageView.load("https://image.tmdb.org/t/p/w500${movie.poster_path}")

        // Set favorite button state
        val movieState = movieStates[movie.id]
        holder.favoriteButton.setImageResource(
            if (movieState?.isFavorite == true) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )

        // Set watched button state
        holder.watchedButton.setImageResource(
            if (movieState?.isWatched == true) R.drawable.ic_watched
            else R.drawable.ic_unwatched
        )

        // Click listeners
        holder.favoriteButton.setOnClickListener {
            onFavoriteClick(movie)
            // Toggle local state immediately for better UX
            movieStates[movie.id] = movieStates[movie.id]?.copy(isFavorite = !(movieStates[movie.id]?.isFavorite ?: false))
                ?: MovieEntity(movie.id, movie.title, movie.poster_path, isFavorite = true)
            notifyItemChanged(position)
        }

        holder.watchedButton.setOnClickListener {
            onWatchedClick(movie)
            // Toggle local state immediately for better UX
            movieStates[movie.id] = movieStates[movie.id]?.copy(isWatched = !(movieStates[movie.id]?.isWatched ?: false))
                ?: MovieEntity(movie.id, movie.title, movie.poster_path, isWatched = true)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }

    fun updateMovieState(movieId: Int, movieEntity: MovieEntity?) {
        movieStates[movieId] = movieEntity
        notifyItemChanged(movies.indexOfFirst { it.id == movieId })
    }
}