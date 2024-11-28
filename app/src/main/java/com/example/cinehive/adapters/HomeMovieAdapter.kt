package com.example.cinehive.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cinehive.R
import com.example.cinehive.data.local.MovieEntity
import com.example.cinehive.dataclasses.Movie

class HomeMovieAdapter(
    private val movies: MutableList<Movie>,
    private val onFavoriteClick: (Movie) -> Unit,
    private val onWatchedClick: (Movie) -> Unit,
    private val onMovieClick: (Movie) -> Unit,
    private val isHorizontal: Boolean = true
) : RecyclerView.Adapter<HomeMovieAdapter.MovieViewHolder>() {

    private val movieStates = mutableMapOf<Int, MovieEntity?>()

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moviePosterImageView: ImageView = itemView.findViewById(R.id.movie_poster)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favorite_button)
        val watchedButton: ImageButton = itemView.findViewById(R.id.watched_button)

        // Optional views for vertical layout
        val movieTitle: TextView? = if (!isHorizontal) itemView.findViewById(R.id.movie_title) else null
        val releaseDate: TextView? = if (!isHorizontal) itemView.findViewById(R.id.release_date) else null
        val ratingText: TextView? = if (!isHorizontal) itemView.findViewById(R.id.rating_text) else null
        val overview: TextView? = if (!isHorizontal) itemView.findViewById(R.id.overview) else null

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMovieClick(movies[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layout = if (isHorizontal) {
            R.layout.home_movie_item
        } else {
            R.layout.search_movie_item
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        val context = holder.itemView.context

        // Load movie poster
        holder.moviePosterImageView.load("https://image.tmdb.org/t/p/w500${movie.poster_path}")

        // Set movie details for vertical layout
        if (!isHorizontal) {
            holder.movieTitle?.text = movie.title

            // Format release date to show just the year
            holder.releaseDate?.text = movie.release_date.split("-").firstOrNull() ?: ""

            // Format rating text with vote count
            holder.ratingText?.text = context.getString(
                R.string.rating_format,
                movie.vote_average,
                formatVoteCount(movie.vote_count)
            )

            holder.overview?.text = movie.overview
        }

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
            movieStates[movie.id] = movieStates[movie.id]?.copy(
                isFavorite = !(movieStates[movie.id]?.isFavorite ?: false)
            ) ?: MovieEntity(
                id = movie.id,
                title = movie.title,
                posterPath = movie.poster_path,
                backdropPath = movie.backdrop_path,
                overview = movie.overview,
                releaseDate = movie.release_date,
                voteAverage = movie.vote_average,
                voteCount = movie.vote_count,
                isFavorite = true
            )
            notifyItemChanged(position)
        }

        holder.watchedButton.setOnClickListener {
            onWatchedClick(movie)
            // Toggle local state immediately for better UX
            movieStates[movie.id] = movieStates[movie.id]?.copy(
                isWatched = !(movieStates[movie.id]?.isWatched ?: false)
            ) ?: MovieEntity(
                id = movie.id,
                title = movie.title,
                posterPath = movie.poster_path,
                backdropPath = movie.backdrop_path,
                overview = movie.overview,
                releaseDate = movie.release_date,
                voteAverage = movie.vote_average,
                voteCount = movie.vote_count,
                isWatched = true
            )
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        val startPosition = movies.size
        movies.clear()
        movies.addAll(newMovies)
        if (startPosition == 0) {
            notifyDataSetChanged()
        } else {
            notifyItemRangeInserted(startPosition, newMovies.size)
        }
    }

    fun updateMovieState(movieId: Int, movieEntity: MovieEntity?) {
        movieStates[movieId] = movieEntity
        notifyItemChanged(movies.indexOfFirst { it.id == movieId })
    }

    private fun formatVoteCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
            count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
            else -> count.toString()
        }
    }
}