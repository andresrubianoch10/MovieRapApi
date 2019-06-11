package com.arubianoch.movierapapi.data.network.dataSource

import androidx.lifecycle.LiveData
import com.arubianoch.movierapapi.data.network.response.MovieResponse

/**
 * @author Andres Rubiano Del Chiaro
 */
interface MovieDataSource {

    val downloadedMovies: LiveData<MovieResponse>
    val downloadedUpcomingMovies: LiveData<MovieResponse>

    suspend fun fetchMovies(
        sortBy: String,
        movieType: String,
        page: Int
    )

    suspend fun upcomingMovies(
        initDate: String,
        lastDate: String,
        movieType: String,
        page: Int
    )
}