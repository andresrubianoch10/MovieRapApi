package com.arubianoch.movierapapi.ui.detailMovie

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.arubianoch.movierapapi.R

class DetailMovieFragment : Fragment() {

    companion object {
        fun newInstance() = DetailMovieFragment()
    }

    private lateinit var viewModel: DetailMovieViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detail_movie_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DetailMovieViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
