package com.example.cinehive.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinehive.dataclasses.Movie
import com.example.cinehive.databinding.MovieListItemBinding

class MovieListAdapter(private var movies: List<Movie> = emptyList()) :
    RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {

    class MovieViewHolder(private val binding: MovieListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movie = movie
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun updateMovies(newMovies: List<Movie>) {
        this.movies = newMovies
        notifyDataSetChanged()
    }
}