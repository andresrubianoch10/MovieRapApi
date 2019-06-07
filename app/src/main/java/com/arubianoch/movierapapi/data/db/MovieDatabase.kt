package com.arubianoch.movierapapi.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.arubianoch.movierapapi.data.db.converter.LocalDateConverter
import com.arubianoch.movierapapi.data.db.dao.MovieDao
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.data.db.entity.MovieMetadata

/**
 * @author Andres Rubiano Del Chiaro
 */

@Database(
    entities = [MovieInfo::class, MovieMetadata::class],
    version = 3
)
@TypeConverters(LocalDateConverter::class)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var instance: MovieDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MovieDatabase::class.java, "movie_table"
            ).fallbackToDestructiveMigration()
                .build()
    }
}