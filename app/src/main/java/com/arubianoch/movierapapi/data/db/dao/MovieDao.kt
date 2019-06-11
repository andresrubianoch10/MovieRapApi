package com.arubianoch.movierapapi.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.data.db.entity.MovieMetadata

/**
 * @author Andres Rubiano Del Chiaro
 */
@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun upsertMovieInfo(movieList: List<MovieInfo>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertMovieMetadata(movieList: MovieMetadata)

    @Query("SELECT * FROM movie_info WHERE movieType = :movieType")
    fun getMoviesByType(movieType: String): LiveData<List<MovieInfo>>

    @Query("SELECT * FROM movie_info WHERE id = :movieId")
    fun getMovieDetail(movieId: String): LiveData<MovieInfo>

    @Query("SELECT * FROM movie_metadata")
    fun getMovieMetada(): MovieMetadata
}