package com.arubianoch.movierapapi.ui.upcoming

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager

import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.data.network.ApiMovieService
import com.arubianoch.movierapapi.ui.adapter.MovieAdapter
import com.arubianoch.movierapapi.ui.base.ScopedFragment
import com.arubianoch.movierapapi.ui.popular.PopularViewModel
import com.arubianoch.movierapapi.ui.popular.PopularViewModelFactory
import kotlinx.android.synthetic.main.popular_fragment.*
import kotlinx.android.synthetic.main.top_rated_fragment.*
import kotlinx.android.synthetic.main.upcoming_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class UpcomingFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: PopularViewModelFactory by instance()
    private var adapter: MovieAdapter? = null

    private lateinit var viewModel: PopularViewModel

    companion object {
        fun newInstance() = UpcomingFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popular_fragment, container, false)
    }

    private fun setUpRecycler(movies: List<MovieInfo>) {
        adapter = MovieAdapter(activity!!, movies as ArrayList<MovieInfo>)
        containerMovie.adapter = adapter
        containerMovie.layoutManager = GridLayoutManager(activity!!, 2)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(PopularViewModel::class.java)

        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val movieResponse = viewModel.upcoming.await()

        movieResponse.observe(viewLifecycleOwner, Observer { movies ->
            if (movies.isNullOrEmpty()) return@Observer

            group_loading.visibility = View.GONE
            setUpRecycler(movies)
        })
    }
}