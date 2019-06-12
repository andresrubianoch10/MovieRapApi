package com.arubianoch.movierapapi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.data.db.entity.MovieInfo
import com.arubianoch.movierapapi.internal.glide.GlideApp
import kotlinx.android.synthetic.main.item_movie.view.*
import kotlinx.android.synthetic.main.item_progress.view.*

/**
 * @author Andres Rubiano Del Chiaro
 */
class MovieAdapter(
    private val context: Context,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var data: MutableList<Any>? = ArrayList()

    companion object {
        private const val TYPE_FAMILY = 0
        private const val TYPE_FRIEND = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)
        return when (viewType) {
            TYPE_FAMILY -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_movie, parent, false)
                Holder(context, view, onItemClickListener, data)
            }
            TYPE_FRIEND -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_progress, parent, false)
                ProgressHolder(view, onItemClickListener)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = data?.get(position)
        when (holder) {
            is Holder -> holder.bind(element as MovieInfo)
            is ProgressHolder -> holder.bind(element as Any)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val comparable = data?.get(position)
        return when (comparable) {
            is MovieInfo -> TYPE_FAMILY
            is Any -> TYPE_FRIEND
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }

    override fun getItemCount(): Int {
        return data?.size!!
    }

    private fun add(mc: MovieInfo) {
        data?.add(mc)
    }

    fun addAll(mcList: List<Any>) {
        if (mcList.size == data!!.size) {
            notifyDataSetChanged()
            return
        }

        val oldLength = data!!.size
        val newLength = mcList.size

        removedOldAddMoreMovies(oldLength)

        mcList.forEach {
            if (!data?.contains(it)!!) {
                add(it as MovieInfo)
            }
        }
        data?.add(Any())

        notifyItemRangeInserted(oldLength - 1, newLength)
    }

    inner class Holder(
        private val context: Context,
        itemView: View?,
        private val onItemClick: OnItemClickListener? = null,
        data: MutableList<Any>?
    ) : BaseViewHolder<MovieInfo>(itemView!!) {

        init {
            itemView!!.setOnClickListener {
                data?.get(adapterPosition)?.let { it1 -> onItemClick?.onItemClicked(it1 as MovieInfo) }
            }
        }

        override fun bind(item: MovieInfo) {
            item.let { movie ->
                itemView.movieTitle.text = movie.title

                GlideApp.with(context)
                    .load("https://image.tmdb.org/t/p/original/${movie.poster_path}")
                    .into(itemView.moviePoster)
            }
        }
    }

    inner class ProgressHolder(
        itemView: View,
        private val onItemClick: OnItemClickListener? = null
        ) : BaseViewHolder<Any>(itemView) {

        override fun bind(item: Any) {
            itemView.fab.setOnClickListener {
                onItemClick?.onAddMoreItems()
                itemView.progressBar.isVisible = true
                itemView.fab.isVisible = false
            }
        }
    }

    private fun removedOldAddMoreMovies(position: Int) {
        if (data?.size!! > 0) {
            data?.removeAt(position - 1)
        }
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClicked(itemView: MovieInfo)

        fun onAddMoreItems()
    }
}