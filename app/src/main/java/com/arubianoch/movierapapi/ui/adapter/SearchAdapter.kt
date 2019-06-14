package com.arubianoch.movierapapi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.internal.glide.GlideApp
import kotlinx.android.synthetic.main.item_movie.view.*

class SearchAdapter(
    private val contactList: ArrayList<MovieInfo>?,
    private val listener: MoviesAdapterListener
) : RecyclerView.Adapter<SearchAdapter.MyViewHolder>(), Filterable {

    var contactListFiltered: MutableList<MovieInfo>? = ArrayList()

    init {
        this.contactListFiltered = contactList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)

        return MyViewHolder(parent.context, itemView, listener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(contactListFiltered!![position])
    }

    override fun getItemCount(): Int = contactListFiltered!!.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = contactList
                } else {
                    val filteredList = ArrayList<MovieInfo>()
                    contactList?.forEach {
                        if (it.title?.toLowerCase()?.contains(charString.toLowerCase())!!) {
                            filteredList.add(it)
                        }
                    }
                    contactListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                contactListFiltered = filterResults.values as ArrayList<MovieInfo>
                notifyDataSetChanged()
            }
        }
    }

    inner class MyViewHolder(
        private val context: Context,
        view: View,
        listener: MoviesAdapterListener
    ) : RecyclerView.ViewHolder(view) {

        init {
            itemView!!.setOnClickListener {
                listener.onMovieSelected(contactListFiltered!![adapterPosition])
            }
        }

        fun bindView(movie: MovieInfo?) {
            movie?.let {
                itemView.movieTitle.text = movie.title

                GlideApp.with(context)
                    .load("https://image.tmdb.org/t/p/original/${movie.poster_path}")
                    .into(itemView.moviePoster)
            }
        }
    }

    interface MoviesAdapterListener {
        fun onMovieSelected(movieInfo: MovieInfo)
    }
}
