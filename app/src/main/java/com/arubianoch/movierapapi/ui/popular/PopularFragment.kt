package com.arubianoch.movierapapi.ui.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.ui.MainActivity
import com.arubianoch.movierapapi.ui.adapter.MovieAdapter
import com.arubianoch.movierapapi.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popular_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class PopularFragment : ScopedFragment(), KodeinAware, MovieAdapter.OnItemClickListener {

    override val kodein by closestKodein()
    private val viewModelFactory: PopularViewModelFactory by instance()
    private var adapter: MovieAdapter? = null

    private lateinit var viewModel: PopularViewModel
    private var lastPosition: Int? = 0

    companion object {
        fun newInstance() = PopularFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popular_fragment, container, false)
    }

    private fun setUpRecycler(movies: List<Any>) {
        if (adapter == null) {
            adapter = MovieAdapter(this@PopularFragment.requireContext(), this@PopularFragment)
        }
        containerMovie.adapter = adapter
        containerMovie.layoutManager = GridLayoutManager(activity!!, 3)
        adapter!!.addAll(movies)

        (containerMovie.layoutManager as GridLayoutManager).scrollToPosition(lastPosition!!)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("position", lastPosition!!)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) lastPosition = savedInstanceState.getInt("position", 0)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(PopularViewModel::class.java)

        bindUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as MainActivity?
        activity?.toolbar?.isVisible = true
    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val movieResponse = viewModel.popular.await()

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
        viewModel.fetchPopular()
        lastPosition = adapter?.itemCount?.minus(6)
    }

    private fun showMovieDetail(id: String) {
        val actionDetail = PopularFragmentDirections.actionDetail(id)
        Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(actionDetail)
    }
}
