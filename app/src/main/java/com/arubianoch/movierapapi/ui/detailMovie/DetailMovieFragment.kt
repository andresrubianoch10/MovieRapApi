package com.arubianoch.movierapapi.ui.detailMovie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.internal.exceptions.DateNotFoundException
import com.arubianoch.movierapapi.internal.glide.GlideApp
import com.arubianoch.movierapapi.ui.MainActivity
import com.arubianoch.movierapapi.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.detail_movie_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory

class DetailMovieFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory
            : ((String) -> MovieDetailViewModelFactory) by factory()

    private lateinit var viewModel: DetailMovieViewModel

    companion object {
        fun newInstance() = DetailMovieFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detail_movie_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbarDetail)
        activity.toolbar.isVisible = false
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val safeArgs = arguments?.let { DetailMovieFragmentArgs.fromBundle(it) }
        val movieId = safeArgs?.movieId ?: throw DateNotFoundException()

        viewModel = ViewModelProviders.of(this, viewModelFactory(movieId))
            .get(DetailMovieViewModel::class.java)

        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val movieSelected = viewModel.detailMovie.await()

        movieSelected.observe(this@DetailMovieFragment, Observer { movie ->
            if (movie == null) return@Observer

            GlideApp.with(this@DetailMovieFragment)
                .load("https://image.tmdb.org/t/p/original/" + movie.poster_path)
                .into(movie_detail_image)

            detail_title.title = movie.title
            vote_average.text = movie.vote_average.toString()
            date_release.text = movie.release_date
            votes.text = movie.vote_count.toString()
            language.text = movie.original_language
            movie_overview.text = movie.overview
            movie_trailer.text = movie.video.toString()
        })
    }
}

