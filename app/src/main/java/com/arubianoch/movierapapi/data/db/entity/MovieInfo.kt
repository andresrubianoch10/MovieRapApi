package com.arubianoch.movierapapi.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author Andres Rubiano Del Chiaro
 */
@Entity(tableName = "movie_info")
data class MovieInfo (
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
) : Comparable<MovieInfo> {

    override fun compareTo(other: MovieInfo): Int {
        if (id > other.id) return 1
        if (id < other.id) return 1
        if (title?.length!! > other.title!!.length) return 1
        if (title.length < other.title.length) {
            return 1
        }
        return 0
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null)
            return false
        if (obj === this)
            return true
        if (obj.javaClass != javaClass) {
            return false
        }

        val rhs = obj as MovieInfo?
        return this.title == rhs!!.title
    }

    lateinit var movieType: String
}