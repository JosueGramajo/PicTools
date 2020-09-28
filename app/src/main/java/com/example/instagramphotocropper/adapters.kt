package com.example.instagramphotocropper

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson

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

class DestinationAdapter(val list : ArrayList<String>, val context : Context) : RecyclerView.Adapter<DestinationAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutFilter = LayoutInflater.from(p0.context).inflate(R.layout.destination_cell, p0, false)
        return ViewHolder(layoutFilter)
    }

    override fun getItemCount(): Int = list.count()

    override fun onBindViewHolder(holder : ViewHolder, p1: Int) = holder.bind(list[p1])

    fun deleteItemAtPosition(position : Int){
        val itemToRemove = list.get(position)
        list.removeAt(position)
        notifyItemRemoved(position)

        val preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key),Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val gson = Gson()
        val existingArray = preferences.getString("paths",null)
        existingArray?.let { existing ->
            val existingList = gson.fromJson(existing, RecentPathList::class.java)
            existingList.list.map {
                if (it.path.equals(itemToRemove)){
                    existingList.list.remove(it)
                }
            }

            val json = gson.toJson(existingList)

            editor.putString("paths", json)

            editor.apply()
        }
    }

    class ViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        fun bind(destination : String) = with(view){
            val textView = view.findViewById(R.id.destinationNameTextView) as TextView
            textView.text = destination
        }
    }
}