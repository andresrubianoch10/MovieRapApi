package com.arubianoch.movierapapi.ui.topRated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.ui.adapter.MovieAdapter
import com.arubianoch.movierapapi.ui.base.ScopedFragment
import com.arubianoch.movierapapi.ui.popular.PopularFragmentDirections
import com.arubianoch.movierapapi.ui.popular.PopularViewModel
import com.arubianoch.movierapapi.ui.popular.PopularViewModelFactory
import kotlinx.android.synthetic.main.popular_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class TopRatedFragment : ScopedFragment(), KodeinAware, MovieAdapter.OnItemClickListener {

    override val kodein by closestKodein()
    private val viewModelFactory: PopularViewModelFactory by instance()
    private var adapter: MovieAdapter? = null

    private lateinit var viewModel: PopularViewModel

    companion object {
        fun newInstance() = TopRatedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popular_fragment, container, false)
    }

    private fun setUpRecycler(movies: List<MovieInfo>) {
        adapter = MovieAdapter(activity!!, this)
        containerMovie.adapter = adapter
        containerMovie.layoutManager = GridLayoutManager(activity!!, 3)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(PopularViewModel::class.java)

        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val movieResponse = viewModel.topRated.await()

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

    }

    private fun showMovieDetail(id: String) {
        val actionDetail = PopularFragmentDirections.actionDetail(id)
        Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(actionDetail)
    }
}
