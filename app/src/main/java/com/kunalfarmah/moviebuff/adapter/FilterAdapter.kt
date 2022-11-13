package com.kunalfarmah.moviebuff.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kunalfarmah.moviebuff.R
import com.kunalfarmah.moviebuff.databinding.FilterItemBinding
import com.kunalfarmah.moviebuff.databinding.ListItemMovieGridBinding
import com.kunalfarmah.moviebuff.listener.FilterClickListener
import com.kunalfarmah.moviebuff.listener.MovieClickListener
import com.kunalfarmah.moviebuff.model.FilterItem
import com.kunalfarmah.moviebuff.preferences.PreferenceManager
import com.kunalfarmah.moviebuff.model.Movie
import com.kunalfarmah.moviebuff.util.Constants

class FilterAdapter(val context: Context?, var list: List<FilterItem>, private val listener: FilterClickListener) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        return FilterViewHolder(
            FilterItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        var genre = list[position]
        holder.bind(genre, position)
        holder.itemView.setOnClickListener {
            listener.onFilterClick(genre, position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = FilterItemBinding.bind(itemView)
        fun bind(genre: FilterItem, pos: Int) {

            binding.genreTv.text = genre.genre
            if(genre.selected) {
                binding.genreCard.setCardBackgroundColor(itemView.context.resources?.getColor(R.color.colorPrimary)!!)
                binding.genreTv.setTextColor(itemView.context.resources?.getColor(R.color.white)!!)
            }
            else{
                binding.genreCard.setCardBackgroundColor(itemView.context.resources?.getColor(R.color.white)!!)
                binding.genreTv.setTextColor(itemView.context.resources?.getColor(R.color.black)!!)
            }
        }
    }


}