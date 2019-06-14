package com.arubianoch.movierapapi.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.ui.adapter.MovieAdapter
import com.arubianoch.movierapapi.ui.base.MovieViewModel
import com.arubianoch.movierapapi.ui.base.MovieViewModelFactory
import com.arubianoch.movierapapi.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.movie_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class UpcomingFragment : ScopedFragment(), KodeinAware, MovieAdapter.OnItemClickListener {

    override val kodein by closestKodein()
    private val viewModelFactory: MovieViewModelFactory by instance()
    private var adapter: MovieAdapter? = null

    private lateinit var viewModel: MovieViewModel
    private var lastPosition: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.movie_fragment, container, false)
    }

    private fun setUpRecycler(movies: List<MovieInfo>) {
        if (adapter == null) {
            adapter = MovieAdapter(this@UpcomingFragment.requireContext(), this)
        }
        containerMovie.adapter = adapter
        containerMovie.layoutManager = GridLayoutManager(activity!!, GRID_AMOUNT)
        adapter!!.addAll(movies)

        (containerMovie.layoutManager as GridLayoutManager).scrollToPosition(lastPosition!!)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(POSITION_KEY, lastPosition!!)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) lastPosition = savedInstanceState.getInt(POSITION_KEY, 0)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MovieViewModel::class.java)

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

    override fun onItemClicked(itemView: MovieInfo) {
        showMovieDetail(itemView.id.toString())
    }

    override fun onAddMoreItems() {
        viewModel.fetchUpcoming()
        lastPosition = adapter?.itemCount?.minus(6)
    }
}