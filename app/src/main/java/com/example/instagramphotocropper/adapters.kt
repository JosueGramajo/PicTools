package com.example.instagramphotocropper

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class GalleryAdapter(val list : List<CustomImage>, val itemClick : (CustomImage) -> Unit) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context).inflate(R.layout.cell, p0, false)
        return ViewHolder(layoutInflater, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) = holder.bind(list[p1])

    override fun getItemCount(): Int = list.count()

    class ViewHolder(val view : View, val itemClick: (CustomImage) -> Unit) : RecyclerView.ViewHolder(view){
        fun bind(image : CustomImage) = with(view){
            val imageView = view.findViewById(R.id.img) as ImageView

            imageView.setImageBitmap(image.image)

            imageView.setOnClickListener{ itemClick(image) }
        }

    }
}