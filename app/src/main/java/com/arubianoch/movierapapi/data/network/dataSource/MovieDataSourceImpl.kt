package com.arubianoch.movierapapi.data.network.dataSource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arubianoch.movierapapi.data.network.ApiMovieService
import com.arubianoch.movierapapi.data.network.response.MovieResponse
import com.arubianoch.movierapapi.internal.exceptions.NoConnectivityException

/**
 * @author Andres Rubiano Del Chiaro
 */
class MovieDataSourceImpl(
    private val apiMovieService: ApiMovieService
) : MovieDataSource {

    private val _downloadedMovies = MutableLiveData<MovieResponse>()
    override val downloadedMovies: LiveData<MovieResponse>
        get() = _downloadedMovies

    private val _downloadedUpcomingMovies = MutableLiveData<MovieResponse>()
    override val downloadedUpcomingMovies: LiveData<MovieResponse>
        get() = _downloadedUpcomingMovies

    override suspend fun fetchMovies(sortBy: String, movieType: String, page: Int) {
        try {
            val fetchedMovies = apiMovieService.getPopularMovies(sortBy, page).await()
            fetchedMovies.results.forEach { it.movieType = movieType}
            _downloadedMovies.postValue(fetchedMovies)
        } catch (e: NoConnectivityException) {
            showLogError(e)
        }
    }

    override suspend fun upcomingMovies(initDate: String, lastDate: String, movieType: String, page: Int) {
        try {
            val upcomingMovies = apiMovieService.getUpcomingMovies(initDate, lastDate, page).await()
            upcomingMovies.results.forEach { it.movieType = movieType }
            _downloadedUpcomingMovies.postValue(upcomingMovies)
        } catch (e: NoConnectivityException) {
            showLogError(e)
        }
    }

    private fun showLogError(e: NoConnectivityException) {
        Log.e("Connectivity", "No internet connection.", e)
    }
}