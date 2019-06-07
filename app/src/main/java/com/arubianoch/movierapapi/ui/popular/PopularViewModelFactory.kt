package com.arubianoch.movierapapi.ui.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arubianoch.movierapapi.data.repository.MovieRepository

/**
 * @author Andres Rubiano Del Chiaro
 */
class PopularViewModelFactory(
    private val movieRepository: MovieRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PopularViewModel(movieRepository) as T
    }
}