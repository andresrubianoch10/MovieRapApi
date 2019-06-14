package com.arubianoch.movierapapi.ui.base

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.ui.popular.PopularFragmentDirections
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * @author Andres Rubiano Del Chiaro
 */

abstract class ScopedFragment : Fragment(), CoroutineScope {
    private lateinit var job: Job

    companion object {
        internal const val POSITION_KEY = "position"
        internal const val GRID_AMOUNT = 3
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = activity?.toolbar
        toolbar?.isVisible = true
        toolbar?.title = getString(R.string.movie_title)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    protected fun showMovieDetail(id: String) {
        val actionDetail = PopularFragmentDirections.actionDetail(id)
        Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(actionDetail)
    }
}