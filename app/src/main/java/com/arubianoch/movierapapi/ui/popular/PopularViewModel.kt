package com.arubianoch.movierapapi.ui.popular

import androidx.lifecycle.ViewModel;
import com.arubianoch.movierapapi.data.repository.MovieRepository
import com.arubianoch.movierapapi.internal.extensions.lazyDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PopularViewModel(
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

    fun fetchPopular() = GlobalScope.launch(Dispatchers.IO) {
        movieRepository.fetchMoreMoviePopular()
    }
}
