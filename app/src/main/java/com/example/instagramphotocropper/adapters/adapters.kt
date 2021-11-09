package com.example.instagramphotocropper.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramphotocropper.objects.CustomImage
import com.example.instagramphotocropper.R
import com.example.instagramphotocropper.objects.RecentPathList
import com.google.gson.Gson
import java.io.File
import kotlin.random.Random
import android.util.DisplayMetrics
import android.widget.LinearLayout


class GalleryAdapter(val list : List<CustomImage>, val itemClick : (CustomImage) -> Unit) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context).inflate(R.layout.cell, p0, false)
        return ViewHolder(
            layoutInflater,
            itemClick
        )
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

class InstaGalleryAdapter(val list : List<String>, val context : Context, val itemClick : (String) -> Unit) : RecyclerView.Adapter<InstaGalleryAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context).inflate(R.layout.cell, p0, false)
        return ViewHolder(
            layoutInflater,
            context,
            itemClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) = holder.bind(list[p1])

    override fun getItemCount(): Int = list.count()

    class ViewHolder(val view : View, val context : Context, val itemClick: (String) -> Unit) : RecyclerView.ViewHolder(view){
        fun bind(imageUrl : String) = with(view){
            val imageView = view.findViewById<ImageView>(R.id.img)
            val containerView = view.findViewById<LinearLayout>(R.id.containerView)

            val currentScreenWidth = getCurrentWidth();
            val params = containerView.layoutParams
            params.height = currentScreenWidth
            params.width = currentScreenWidth
            containerView.layoutParams = params

            Glide.with(context).load(imageUrl).centerCrop().into(imageView)

            imageView.setOnClickListener{ itemClick(imageUrl) }
        }

        private fun getCurrentWidth() : Int{
            val displayMetrics = context.resources.displayMetrics
            return (displayMetrics.widthPixels / displayMetrics.density).toInt()
        }

    }
}

class DestinationAdapter(val list : ArrayList<String>, val context : Context) : RecyclerView.Adapter<DestinationAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutFilter = LayoutInflater.from(p0.context).inflate(R.layout.destination_cell, p0, false)
        return ViewHolder(
            layoutFilter
        )
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
            textView.text = destination.split("/").last()

            val imageView = view.findViewById<ImageView>(R.id.imageView3)
            imageView.setImageBitmap(getRandomImage(destination))
        }
        private fun getRandomImage(path : String) : Bitmap{
            val directory = File(path)
            val fileList = directory.listFiles()
            val randomFile = fileList.get(Random.nextInt(0, fileList.size - 1))
            return BitmapFactory.decodeFile(randomFile.path)
        }
    }
}