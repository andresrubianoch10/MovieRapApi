package com.arubianoch.movierapapi.data.network.response

import com.arubianoch.movierapapi.data.db.entity.MovieInfo

/**
 * @author Andres Rubiano Del Chiaro
 */
data class MovieResponse(
    val id: Int,
    val page: Int,
    val results: List<MovieInfo>,
    val total_pages: Int,
    val total_results: Int
)