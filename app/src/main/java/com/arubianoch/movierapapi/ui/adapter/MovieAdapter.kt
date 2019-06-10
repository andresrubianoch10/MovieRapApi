package com.arubianoch.movierapapi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.internal.glide.GlideApp
import kotlinx.android.synthetic.main.item_movie.view.*

/**
 * @author Andres Rubiano Del Chiaro
 */
class MovieAdapter(
    private val context: Context,
    private var data: ArrayList<MovieInfo>,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MovieAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)

        return Holder(context, view, onItemClickListener, data)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(data[position])
    }

    class Holder(private val context: Context,
        itemView: View?,
        private val onItemClick: OnItemClickListener? = null,
        data: ArrayList<MovieInfo>
    ) :
        RecyclerView.ViewHolder(itemView!!) {

        init {
            itemView!!.setOnClickListener {
                onItemClick?.onItemClicked(data[adapterPosition])
            }
        }

        fun bindView(itemMovie: MovieInfo?) {
            itemMovie?.let { movie ->
                itemView.movieTitle.text = movie.title

                GlideApp.with(context)
                    .load("https://image.tmdb.org/t/p/original/${movie.poster_path}")
                    .into(itemView.moviePoster)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(itemView: MovieInfo)
    }
}