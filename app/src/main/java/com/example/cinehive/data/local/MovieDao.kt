package com.example.cinehive.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY addedDate DESC")
    fun getAllMovies(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isFavorite = 1 ORDER BY addedDate DESC")
    fun getFavoriteMovies(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isWatched = 1 ORDER BY addedDate DESC")
    fun getWatchedMovies(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE rating IS NOT NULL ORDER BY rating DESC")
    fun getRatedMovies(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isFavorite = 1 ORDER BY addedDate DESC")
    suspend fun getFavoriteMoviesDirect(): List<MovieEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity?

    @Query("DELETE FROM movies WHERE id = :movieId")
    suspend fun deleteMovie(movieId: Int)
}