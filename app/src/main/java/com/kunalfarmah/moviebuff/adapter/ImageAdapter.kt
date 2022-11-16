package com.kunalfarmah.moviebuff.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kunalfarmah.moviebuff.databinding.ItemImageBinding
import com.kunalfarmah.moviebuff.retrofit.PostersItem

class ImageAdapter(context: Context, images: List<PostersItem>?) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    var list = images
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context),parent,false).root)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(list!![position])
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemImageBinding?=null
        init{
            binding = ItemImageBinding.bind(itemView)
        }

        companion object {
            private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
        }

        fun bind(postersItem: PostersItem){
            binding?.imageView?.let {
                Glide.with(itemView.context).load(IMAGE_BASE_URL+postersItem.filePath).diskCacheStrategy(
                    DiskCacheStrategy.RESOURCE).into(it)
            }
        }
    }
}