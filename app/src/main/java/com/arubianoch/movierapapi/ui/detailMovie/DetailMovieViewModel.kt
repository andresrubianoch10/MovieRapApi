package com.arubianoch.movierapapi.ui.detailMovie

import androidx.lifecycle.ViewModel
import com.arubianoch.movierapapi.data.repository.MovieRepository
import com.arubianoch.movierapapi.internal.extensions.lazyDeferred

class DetailMovieViewModel(
    private val movieId: String,
    private val movieRepository: MovieRepository
) : ViewModel() {

    val detailMovie by lazyDeferred {
        movieRepository.getMovieDetail(movieId)
    }
}
