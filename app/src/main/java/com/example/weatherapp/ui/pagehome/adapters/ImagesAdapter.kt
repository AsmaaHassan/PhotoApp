package com.example.weatherapp.ui.pagehome.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R

class ImagesAdapter(
    val imagesList: ArrayList<String>
) :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {
    val TAG = "ImagesAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return ImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }


    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.itemView.setOnClickListener(View.OnClickListener {
        })

        return holder.bind(imagesList[position])
    }


    class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val TAG = "ImagesViewHolder"
        private val imPost: ImageView = itemView.findViewById(R.id.imPostItem)


        fun bind(post: String) {
            Log.i(TAG, "bind() - post: " + post)
            Glide.with(itemView.context).load(post)
                .into(imPost)
        }
    }

}