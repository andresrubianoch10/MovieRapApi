package com.arubianoch.movierapapi.ui.search

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.ui.adapter.SearchAdapter
import com.arubianoch.movierapapi.ui.base.MovieViewModel
import com.arubianoch.movierapapi.ui.base.MovieViewModelFactory
import com.arubianoch.movierapapi.ui.base.ScopedActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

/**
 * @author Andres Rubiano Del Chiaro
 */
class SearchActivity : ScopedActivity(),
    SearchAdapter.MoviesAdapterListener,
    KodeinAware {

    //region Constants and variables
    override val kodein by closestKodein()
    private var searchView: SearchView? = null
    private var searchAdapter: SearchAdapter? = null
    private val viewModelFactory: MovieViewModelFactory by instance()

    private lateinit var viewModel: MovieViewModel

    companion object {
        internal const val ID_SEARCH = "search_id"
    }
    //endregion

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(my_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.options)

        viewModel = ViewModelProviders.of(this@SearchActivity, viewModelFactory).get(MovieViewModel::class.java)

        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val movieResponse = viewModel.allMovies.await()

        movieResponse.observe(this@SearchActivity, Observer { movies ->
            if (movies.isNullOrEmpty()) return@Observer
            setUpRecycler(movies as ArrayList<MovieInfo>)
        })
    }
    //endregion

    //region Methods
    override fun onMovieSelected(movieInfo: MovieInfo) {
        val intent = Intent()
        intent.putExtra(ID_SEARCH, movieInfo.id)
        setResult(Activity.RESULT_OK, intent)
        finish()
        overridePendingTransition(R.animator.back_in, R.animator.back_out)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.menu_item_search)?.actionView as SearchView
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView!!.maxWidth = Integer.MAX_VALUE

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchAdapter!!.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchAdapter!!.filter.filter(newText)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_item_search -> true
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!searchView!!.isIconified) {
            searchView!!.isIconified = true
            return
        }
        super.onBackPressed()
        overridePendingTransition(R.animator.back_in, R.animator.back_out)
    }

    private fun setUpRecycler(movies: ArrayList<MovieInfo>) {
        if (searchAdapter == null) {
            searchAdapter = SearchAdapter(movies, this@SearchActivity)
        }
        recycler_view!!.layoutManager = GridLayoutManager(this@SearchActivity, 3)
        recycler_view!!.itemAnimator = DefaultItemAnimator()
        recycler_view!!.adapter = searchAdapter
    }
    //endregion
}