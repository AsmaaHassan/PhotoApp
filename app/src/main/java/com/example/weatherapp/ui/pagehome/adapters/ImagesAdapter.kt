package com.example.weatherapp.ui.pagehome.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.data.internal.PostEntity
import kotlinx.android.synthetic.main.post_item.view.*

class ImagesAdapter(
    val imagesList: List<PostEntity>,
    val context: Context?
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

        return holder.bind(imagesList[position], context)
    }


    class ImagesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val TAG = "ImagesViewHolder"
        private val imPost: ImageView = itemView.findViewById(R.id.imPostItem)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)


        fun bind(post: PostEntity, context: Context?) {
            Log.i(TAG, "bind() - post: " + post)
            Glide.with(itemView.context).load(post.image_path)
                .into(imPost)
            itemView.tvDate.text = post.date
            //set click listener
            itemView.setOnClickListener(View.OnClickListener {
                if (context != null)
                    dialogSignature(post.image_path, context!!).show()
            })
        }


        private fun dialogSignature(url: String?, context: Context): Dialog {
            var dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialoge_image)
            val imPostItem = dialog.findViewById<ImageView>(R.id.imPostItemDialoge)//added this line
            Glide.with(context)
                .load(url)
                .into(imPostItem) //added this line
            dialog.getWindow()
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            return dialog

        }
    }

}