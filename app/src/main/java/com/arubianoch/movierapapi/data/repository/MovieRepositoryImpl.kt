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

    companion object {
        private const val POPULAR_TYPE = "popular"
        private const val TOP_RATED_TYPE = "topRated"
        private const val UPCOMING_TYPE = "upcoming"

        private const val INITIAL_VALUE = 0
        private const val INCREASE_VALUE = 1
        private const val DEFAULT_PAGE = 1
    }


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
                    POPULAR_TYPE -> currentPopularPage?.plus(INCREASE_VALUE)
                    TOP_RATED_TYPE -> currentTopRatedPage?.plus(INCREASE_VALUE)
                    UPCOMING_TYPE -> currentUpcomingPage?.plus(INCREASE_VALUE)
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
                        INCREASE_VALUE,
                        INITIAL_VALUE,
                        INITIAL_VALUE,
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
            return@withContext movieDao.getMoviesByType(TOP_RATED_TYPE)
        }
    }

    override suspend fun getMoviesByPopular(): LiveData<List<MovieInfo>> {
        fetchPopular()
        return withContext(Dispatchers.IO) {
            return@withContext movieDao.getMoviesByType(POPULAR_TYPE)
        }
    }

    override suspend fun getMoviesByUpcoming(): LiveData<List<MovieInfo>> {
        fetchUpcoming()
        return withContext(Dispatchers.IO) {
            return@withContext movieDao.getMoviesByType(UPCOMING_TYPE)
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
        var nextPage = DEFAULT_PAGE
        if (isDataInitialized()) {
            val lastInfoDownloaded = getLastMovieMetadataDownloaded()
            nextPage = lastInfoDownloaded.pagePopular?.plus(INCREASE_VALUE)!!
            lastInfoDownloaded.pagePopular = nextPage
            upsertMovieMetadata(lastInfoDownloaded)
        }
        movieDataSource.fetchMovies("popularity.desc", POPULAR_TYPE, nextPage)
    }

    private suspend fun fetchTopRated() {
        var nextPage = DEFAULT_PAGE
        if (isDataInitialized()) {
            val lastInfoDownloaded = getLastMovieMetadataDownloaded()
            nextPage = lastInfoDownloaded.pageTopRated?.plus(INCREASE_VALUE)!!
            lastInfoDownloaded.pageTopRated = nextPage
            upsertMovieMetadata(lastInfoDownloaded)
        }
        movieDataSource.fetchMovies("vote_average.desc", TOP_RATED_TYPE, nextPage)
    }

    private suspend fun fetchUpcoming() {
        var nextPage = 1
        if (isDataInitialized()) {
            val lastInfoDownloaded = getLastMovieMetadataDownloaded()
            nextPage = lastInfoDownloaded.pageUpcoming?.plus(1)!!
            lastInfoDownloaded.pageUpcoming = nextPage
            upsertMovieMetadata(lastInfoDownloaded)
        }
        //TODO: Change into take period of six months depends on current date
        movieDataSource.upcomingMovies("2019-06-15", "2019-12-15", UPCOMING_TYPE, nextPage)
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

    override suspend fun getAllMovies(): LiveData<List<MovieInfo>> {
        return withContext(Dispatchers.IO) {
            return@withContext movieDao.getAllMovies()
        }
    }
}
