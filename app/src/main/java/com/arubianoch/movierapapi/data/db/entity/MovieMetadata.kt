package com.arubianoch.movierapapi.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

/**
 * @author Andres Rubiano Del Chiaro
 */
const val MOVIE_INFO_ID = 0

@Entity(tableName = "movie_metadata")
data class MovieMetadata(
    var pagePopular: Int?,
    var pageTopRated: Int?,
    var pageUpcoming: Int?,
    val total_pages: Int?,
    val total_results: Int?
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = MOVIE_INFO_ID
    val zonedDateTime: ZonedDateTime
        get() {
            return ZonedDateTime.now()
        }
}