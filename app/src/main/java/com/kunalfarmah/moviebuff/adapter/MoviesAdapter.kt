package com.kunalfarmah.moviebuff.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kunalfarmah.moviebuff.databinding.ListItemMovieGridBinding
import com.kunalfarmah.moviebuff.listener.MovieClickListener
import com.kunalfarmah.moviebuff.room.MovieEntity

class MoviesAdapter(context: Context?, list: List<MovieEntity>, listener:MovieClickListener) :
    RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {

    var movieList: List<MovieEntity> = list
    var mContext: Context? = context
    var movieClick = listener



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        return MoviesViewHolder(
            ListItemMovieGridBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        var movie = movieList[position]
        holder.bind(movie)
        holder.itemView.setOnClickListener { movieClick.onMovieClick(movie.id,holder.binding!!.image) }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ListItemMovieGridBinding? = null

        init {
            binding = ListItemMovieGridBinding.bind(itemView)

        }

        companion object {
            private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
        }

        fun bind(movie: MovieEntity) {
            Glide.with(itemView.context).load(IMAGE_BASE_URL + movie.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding!!.image)
            binding!!.title.text = movie.title
        }
    }


}