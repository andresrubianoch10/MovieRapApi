package com.arubianoch.movierapapi.data.repository

import androidx.lifecycle.LiveData
import com.arubianoch.movierapapi.data.db.dao.MovieDao
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.data.db.entity.MovieMetadata
import com.arubianoch.movierapapi.data.network.dataSource.MovieDataSource
import com.arubianoch.movierapapi.data.network.response.MovieResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime

/**
 * @author Andres Rubiano Del Chiaro
 */
class MovieRepositoryImpl(
    private val movieDao: MovieDao,
    private val movieDataSource: MovieDataSource
) : MovieRepository {

    init {
        movieDataSource.apply {
            downloadedMovies.observeForever { persistMovies(it) }
            downloadedUpcomingMovies.observeForever { persistMovies(it) }
        }
    }

    private fun persistMovies(movies: MovieResponse?) {
        GlobalScope.launch(Dispatchers.IO) {
            val lastInfoDownloaded = getLastMovieMetadataDownloaded()

            if (lastInfoDownloaded != null) {
                var currentPopularPage = lastInfoDownloaded.pagePopular
                var currentTopRatedPage = lastInfoDownloaded.pageTopRated
                var currentUpcomingPage = lastInfoDownloaded.pageUpcoming

                val movie = movies?.results?.get(0)
                when (movie?.movieType) {
                    "popular" -> currentPopularPage?.plus(1)
                    "topRated" -> currentTopRatedPage?.plus(1)
                    "upcoming" -> currentUpcomingPage?.plus(1)
                }

                movieDao.upsertMovieMetadata(
                    MovieMetadata(
                        currentPopularPage,
                        currentTopRatedPage,
                        currentUpcomingPage,
                        movies?.total_pages,
                        movies?.total_results
                    )
                )
            } else {
                movieDao.upsertMovieMetadata(
                    MovieMetadata(
                        1,
                        0,
                        0,
                        movies?.total_pages,
                        movies?.total_results
                    )
                )
            }
            movieDao.upsertMovieInfo(movies?.results)
        }
    }

    override suspend fun getMoviesByTopRated(): LiveData<List<MovieInfo>> {
        fetchTopRated()
        return withContext(Dispatchers.IO) {
            return@withContext movieDao.getMoviesByType("topRated")
        }
    }

    override suspend fun getMoviesByPopular(): LiveData<List<MovieInfo>> {
        fetchPopular()
        return withContext(Dispatchers.IO) {
            return@withContext movieDao.getMoviesByType("popular")
        }
    }

    override suspend fun getMoviesByUpcoming(): LiveData<List<MovieInfo>> {
        fetchUpcoming()
        return withContext(Dispatchers.IO) {
            return@withContext movieDao.getMoviesByType("upcoming")
        }
    }

    override suspend fun getMovieDetail(movieId: String): LiveData<MovieInfo> {
        return withContext(Dispatchers.IO) {
            return@withContext movieDao.getMovieDetail(movieId)
        }
    }

    private fun isDataInitialized(): Boolean {
        return getLastMovieMetadataDownloaded() != null
    }

    private fun upsertMovieMetadata(lastInfoDownloaded: MovieMetadata) {
        movieDao.upsertMovieMetadata(lastInfoDownloaded)
    }

    private suspend fun fetchPopular() {
        var nextPage = 1
        if (isDataInitialized()) {
            val lastInfoDownloaded = getLastMovieMetadataDownloaded()
            nextPage = lastInfoDownloaded.pagePopular?.plus(1)!!
            lastInfoDownloaded.pagePopular = nextPage
            upsertMovieMetadata(lastInfoDownloaded)
        }
        movieDataSource.fetchMovies("popularity.desc", "popular", nextPage)
    }

    private suspend fun fetchTopRated() {
        var nextPage = 1
        if (isDataInitialized()) {
            val lastInfoDownloaded = getLastMovieMetadataDownloaded()
            nextPage = lastInfoDownloaded.pageTopRated?.plus(1)!!
            lastInfoDownloaded.pageTopRated = nextPage
            upsertMovieMetadata(lastInfoDownloaded)
        }
        movieDataSource.fetchMovies("vote_average.desc", "topRated", nextPage)
    }

    private suspend fun fetchUpcoming() {
        var nextPage = 1
        if (isDataInitialized()) {
            val lastInfoDownloaded = getLastMovieMetadataDownloaded()
            nextPage = lastInfoDownloaded.pageUpcoming?.plus(1)!!
            lastInfoDownloaded.pageUpcoming = nextPage
            upsertMovieMetadata(lastInfoDownloaded)
        }
        movieDataSource.upcomingMovies("2019-06-15", "2019-09-15", "upcoming", nextPage)
    }

    private fun getLastMovieMetadataDownloaded(): MovieMetadata = movieDao.getMovieMetada()

    private fun isFetchMovieNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    override suspend fun fetchMoreMoviePopular() {
        fetchPopular()
    }

    override suspend fun fetchMoreMovieTopRated() {
        fetchTopRated()
    }

    override suspend fun fetchMoreMovieUpcoming() {
        fetchUpcoming()
    }
}
