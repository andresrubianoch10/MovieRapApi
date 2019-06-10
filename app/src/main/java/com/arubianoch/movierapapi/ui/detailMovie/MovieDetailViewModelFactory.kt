package com.arubianoch.movierapapi.ui.detailMovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arubianoch.movierapapi.data.repository.MovieRepository


class MovieDetailViewModelFactory (
    private val movieId: String,
    private val movieRepository: MovieRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailMovieViewModel(movieId, movieRepository) as T
    }
}