package com.arubianoch.movierapapi

import android.app.Application
import com.arubianoch.movierapapi.data.db.MovieDatabase
import com.arubianoch.movierapapi.data.network.ApiMovieService
import com.arubianoch.movierapapi.data.network.connectivity.ConnectivityInterceptor
import com.arubianoch.movierapapi.data.network.connectivity.ConnectivityInterceptorImpl
import com.arubianoch.movierapapi.data.network.dataSource.MovieDataSource
import com.arubianoch.movierapapi.data.network.dataSource.MovieDataSourceImpl
import com.arubianoch.movierapapi.data.repository.MovieRepository
import com.arubianoch.movierapapi.data.repository.MovieRepositoryImpl
import com.arubianoch.movierapapi.ui.detailMovie.MovieDetailViewModelFactory
import com.arubianoch.movierapapi.ui.popular.PopularViewModelFactory
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*
import org.threeten.bp.LocalDate

/**
 * @author Andres Rubiano Del Chiaro
 */
class MovieApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@MovieApplication))

        bind() from singleton { MovieDatabase(instance()) }
        bind() from singleton { instance<MovieDatabase>().movieDao() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { ApiMovieService(instance()) }
        bind<MovieDataSource>() with singleton { MovieDataSourceImpl(instance()) }
        bind<MovieRepository>() with singleton {
            MovieRepositoryImpl(
                instance(),
                instance()
            )
        }
        bind() from provider { PopularViewModelFactory(instance()) }
        bind() from factory { movieId: String -> MovieDetailViewModelFactory(movieId, instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}