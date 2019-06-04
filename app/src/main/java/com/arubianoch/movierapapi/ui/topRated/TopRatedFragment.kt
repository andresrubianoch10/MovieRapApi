package com.arubianoch.movierapapi.ui.topRated

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.arubianoch.movierapapi.R

class TopRatedFragment : Fragment() {

    companion object {
        fun newInstance() = TopRatedFragment()
    }

    private lateinit var viewModel: TopRatedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.top_rated_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TopRatedViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
