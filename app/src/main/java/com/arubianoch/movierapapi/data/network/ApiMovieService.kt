package com.arubianoch.movierapapi.data.network

import com.arubianoch.movierapapi.data.network.connectivity.ConnectivityInterceptor
import com.arubianoch.movierapapi.data.network.response.MovieResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Andres Rubiano Del Chiaro
 */
private const val URL = "https://api.themoviedb.org/3/"
private const val API_KEY = "0266c46c25aa2fd93373aba4f48e0fe8"

interface ApiMovieService {

    @GET("discover/movie")
    fun getPopularMovies(
        @Query("sort_by") sortBy: String,
        @Query("page") page: Int
    ): Deferred<MovieResponse>

    @GET("discover/movie")
    fun getUpcomingMovies(
        @Query("primary_release_date.gte") initDate: String,
        @Query("primary_release_date.lte") finishDate: String,
        @Query("page") page: Int
    ): Deferred<MovieResponse>

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): ApiMovieService {
            val requestInterceptor = Interceptor { chain ->

                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiMovieService::class.java)
        }
    }
}