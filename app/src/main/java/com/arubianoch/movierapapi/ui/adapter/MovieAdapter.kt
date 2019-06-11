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
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MovieAdapter.Holder>() {

    private var data: ArrayList<MovieInfo>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)

        return Holder(context, view, onItemClickListener, data)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(data?.get(position))
    }

    override fun getItemCount(): Int {
        return data?.size!!
    }

    private fun add(mc: MovieInfo) {
        data?.add(mc)
//        notifyItemInserted(data!!.size.minus(1))
    }

    fun addAll(mcList: List<MovieInfo>) {
        if (mcList.size == data!!.size) {
            notifyDataSetChanged()
            return
        }

        val oldLength = data!!.size
        val newLength = mcList.size

        mcList.forEach {
            if (!data?.contains(it)!!) {
                add(it)
            }
        }

        notifyItemRangeInserted(oldLength, newLength)
    }

    class Holder(
        private val context: Context,
        itemView: View?,
        private val onItemClick: OnItemClickListener? = null,
        data: ArrayList<MovieInfo>?
    ) :
        RecyclerView.ViewHolder(itemView!!) {

        init {
            itemView!!.setOnClickListener {
                data?.get(adapterPosition)?.let { it1 -> onItemClick?.onItemClicked(it1) }
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