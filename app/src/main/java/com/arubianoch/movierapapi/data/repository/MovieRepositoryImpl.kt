package com.arubianoch.movierapapi.data.repository

import androidx.lifecycle.LiveData
import com.arubianoch.movierapapi.data.db.dao.MovieDao
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.data.db.entity.MovieMetadata
import com.arubianoch.movierapapi.data.network.dataSource.MovieDataSource
import com.arubianoch.movierapapi.data.network.response.MovieResponse
import kotlinx.coroutines.*
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
            movieDao.upsertMovieMetadata(
                MovieMetadata(movies?.page, 0, 0, movies?.total_pages, movies?.total_results)
            )
            movieDao.upsertMovieInfo(movies?.results)
        }
    }

    override suspend fun getMoviesByTopRated(): LiveData<List<MovieInfo>> {
        initMovieData()
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
        initMovieData()
        return withContext(Dispatchers.IO) {
            return@withContext movieDao.getMoviesByType("upcoming")
        }
    }

    override suspend fun getMovieDetail(movieId: String): LiveData<MovieInfo> {
        return withContext(Dispatchers.IO) {
            return@withContext movieDao.getMovieDetail(movieId)
        }
    }

    private fun initMovieData() = runBlocking {
//        val lastInfoDownloaded: MovieMetadata? = movieDao.getMovieMetada()
//            fetchPopular()
//        if (lastInfoDownloaded == null) {
//            fetchTopRated(1)
//            fetchUpcoming("2019-06-15", "2019-09-15", 1)

//        } else {
//            if (isFetchMovieNeeded(lastInfoDownloaded.zonedDateTime)) {

//            fetchTopRated(nextPage!!)
//            fetchUpcoming("2019-06-15", "2019-09-15", nextPage)
//            fetchPopular(nextPage)
//        }

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

    private fun getLastMovieMetadataDownloaded(): MovieMetadata = movieDao.getMovieMetada()

    private suspend fun fetchUpcoming(initDate: String, finishDate: String, page: Int) {
        movieDataSource.upcomingMovies(initDate, finishDate, "upcoming", page)
    }

    private suspend fun fetchTopRated(page: Int) {
        movieDataSource.fetchMovies("vote_average.desc", "topRated", page)
    }

    private fun isFetchMovieNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    override suspend fun fetchMoreMoviePopular() {
        fetchPopular()
    }
}
