package com.arubianoch.movierapapi.ui.popular

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.ui.adapter.MovieAdapter
import com.arubianoch.movierapapi.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.popular_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class PopularFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: PopularViewModelFactory by instance()
    private var adapter: MovieAdapter? = null

    private lateinit var viewModel: PopularViewModel

    companion object {
        fun newInstance() = PopularFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.popular_fragment, container, false)
        return view
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
        val movieResponse = viewModel.popular.await()

        movieResponse.observe(viewLifecycleOwner, Observer { movies ->
            if (movies.isNullOrEmpty()) return@Observer

            group_loading.visibility = View.GONE
            setUpRecycler(movies)
        })
    }
}
