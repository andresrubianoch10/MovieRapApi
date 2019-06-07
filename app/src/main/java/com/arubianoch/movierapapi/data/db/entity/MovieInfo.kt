package com.arubianoch.movierapapi.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author Andres Rubiano Del Chiaro
 */
@Entity(tableName = "movie_info")
data class MovieInfo(
    val adult: Boolean?,
    val backdrop_path: String?,
//    @Embedded
//    val genre_ids: List<Int>,
    @PrimaryKey(autoGenerate = true)
    val customId: Int,
    val id: Int,
    val original_language: String?,
    val original_title: String?,
    val overview: String?,
    val popularity: Double?,
    val poster_path: String?,
    val release_date: String?,
    val title: String?,
    val video: Boolean?,
    val vote_average: Double?,
    val vote_count: Int?
) {
    lateinit var movieType: String
}