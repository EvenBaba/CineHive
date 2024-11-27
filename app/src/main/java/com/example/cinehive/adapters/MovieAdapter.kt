package com.example.cinehive.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cinehive.R
import com.example.cinehive.dataclasses.Movie

class MovieAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moviePosterImageView: ImageView = view.findViewById(R.id.movie_poster)
        val movieTitleTextView: TextView = view.findViewById(R.id.movie_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.movieTitleTextView.text = movie.title
        holder.moviePosterImageView.load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        (movies as MutableList).clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }
}