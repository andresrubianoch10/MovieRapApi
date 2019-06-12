package com.arubianoch.movierapapi.data.repository

import androidx.lifecycle.LiveData
import com.arubianoch.movierapapi.data.db.entity.MovieInfo

/**
 * @author Andres Rubiano Del Chiaro
 */
interface MovieRepository {

    suspend fun getMoviesByTopRated(): LiveData<List<MovieInfo>>

    suspend fun getMoviesByPopular(): LiveData<List<MovieInfo>>

    suspend fun getMoviesByUpcoming(): LiveData<List<MovieInfo>>

    suspend fun getMovieDetail(movieId: String): LiveData<MovieInfo>

    suspend fun fetchMoreMoviePopular()

    suspend fun fetchMoreMovieTopRated()

    suspend fun fetchMoreMovieUpcoming()
}