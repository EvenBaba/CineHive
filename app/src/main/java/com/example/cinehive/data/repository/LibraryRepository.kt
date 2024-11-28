package com.example.cinehive.data.repository

import com.example.cinehive.data.local.MovieDao
import com.example.cinehive.data.local.MovieEntity
import com.example.cinehive.dataclasses.Movie

class LibraryRepository(private val movieDao: MovieDao) {
    val allMovies = movieDao.getAllMovies()
    val favoriteMovies = movieDao.getFavoriteMovies()
    val watchedMovies = movieDao.getWatchedMovies()
    val ratedMovies = movieDao.getRatedMovies()

    suspend fun addToLibrary(
        movie: Movie,
        isFavorite: Boolean = false,
        isWatched: Boolean = false,
        rating: Int? = null
    ) {
        val movieEntity = MovieEntity(
            id = movie.id,
            title = movie.title,
            posterPath = movie.poster_path,
            backdropPath = movie.backdrop_path,
            overview = movie.overview,
            releaseDate = movie.release_date,
            voteAverage = movie.vote_average,
            voteCount = movie.vote_count,
            isFavorite = isFavorite,
            isWatched = isWatched,
            rating = rating
        )
        movieDao.insertMovie(movieEntity)
    }

    suspend fun toggleFavorite(movieId: Int) {
        movieDao.getMovieById(movieId)?.let { movie ->
            movieDao.updateMovie(movie.copy(isFavorite = !movie.isFavorite))
        }
    }

    suspend fun toggleWatched(movieId: Int) {
        movieDao.getMovieById(movieId)?.let { movie ->
            movieDao.updateMovie(movie.copy(isWatched = !movie.isWatched))
        }
    }

    suspend fun rateMovie(movieId: Int, rating: Int) {
        movieDao.getMovieById(movieId)?.let { movie ->
            movieDao.updateMovie(movie.copy(rating = rating))
        }
    }

    suspend fun removeFromLibrary(movieId: Int) {
        movieDao.deleteMovie(movieId)
    }

    suspend fun getMovieById(movieId: Int): MovieEntity? {
        return movieDao.getMovieById(movieId)
    }
}