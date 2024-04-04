package com.example.filmscollection.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.filmscollection.R
import com.google.firebase.storage.FirebaseStorage

class SliderAdapter : RecyclerView.Adapter<SliderAdapter.ImageSliderViewHolder>() {

    private val images = mutableListOf<String>()

    fun addImage(imageUrl: String) {
        images.add(imageUrl)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)
        return ImageSliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageSliderViewHolder, position: Int) {
        val a = FirebaseStorage.getInstance().getReferenceFromUrl(images[position])
        val b = images[position]
        Glide.with(holder.itemView)
            .load(b)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ImageSliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}