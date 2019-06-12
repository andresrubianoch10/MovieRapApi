package com.arubianoch.movierapapi.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Andres Rubiano Del Chiaro
 */
abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)
}