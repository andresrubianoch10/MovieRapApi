package com.arubianoch.movierapapi.ui.base

import androidx.lifecycle.ViewModel;
import com.arubianoch.movierapapi.data.repository.MovieRepository
import com.arubianoch.movierapapi.internal.extensions.lazyDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MovieViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    val popular by lazyDeferred {
        movieRepository.getMoviesByPopular()
    }

    val topRated by lazyDeferred {
        movieRepository.getMoviesByTopRated()
    }

    val upcoming by lazyDeferred {
        movieRepository.getMoviesByUpcoming()
    }

    val allMovies by lazyDeferred {
        movieRepository.getAllMovies()
    }

    fun fetchPopular() = GlobalScope.launch(Dispatchers.IO) {
        movieRepository.fetchMoreMoviePopular()
    }

    fun fetchTopRated() = GlobalScope.launch(Dispatchers.IO) {
        movieRepository.fetchMoreMovieTopRated()
    }
}
