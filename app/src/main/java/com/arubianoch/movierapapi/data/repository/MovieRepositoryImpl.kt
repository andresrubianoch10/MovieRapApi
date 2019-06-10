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
                MovieMetadata(movies?.page, movies?.total_pages, movies?.total_results)
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
        initMovieData()
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
        val lastInfoDownloaded: MovieMetadata? = movieDao.getMovieMetada()

        if (lastInfoDownloaded == null) {
            fetchTopRated(1)
            fetchUpcoming("2019-06-15", "2019-09-15")
            fetchPopular(1)
        } else {
//            if (isFetchMovieNeeded(lastInfoDownloaded.zonedDateTime)) {
            val nextPage = lastInfoDownloaded.page?.plus(1)
            fetchTopRated(nextPage)
            fetchUpcoming("2019-06-15", "2019-09-15")
            fetchPopular(nextPage)

            lastInfoDownloaded.page = nextPage?.plus(1)
            upsertMovieMetada(lastInfoDownloaded)
        }

    }

    private fun upsertMovieMetada(lastInfoDownloaded: MovieMetadata) {
        movieDao.upsertMovieMetadata(lastInfoDownloaded)
    }

    private suspend fun fetchPopular(page: Int?) {
        movieDataSource.fetchMovies("popularity.desc", "popular")
    }

    private suspend fun fetchUpcoming(initDate: String, finishDate: String) {
        movieDataSource.upcomingMovies(initDate, finishDate, "upcoming")
    }

    private suspend fun fetchTopRated(page: Int?) {
        movieDataSource.fetchMovies("vote_average.desc", "topRated")
    }

    private fun isFetchMovieNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
}
