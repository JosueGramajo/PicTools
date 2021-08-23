package com.example.instagramphotocropper.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramphotocropper.adapters.DestinationAdapter
import com.example.instagramphotocropper.R
import com.example.instagramphotocropper.objects.RecentPathList
import com.example.instagramphotocropper.callbacks.SwipeToDeleteCallback
import com.google.gson.Gson
import kotlinx.android.synthetic.main.destination_management_activity.*

class DestinationsManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.destination_management_activity)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView(){
        val paths = arrayListOf<String>()
        val preferences = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE)
        val existingArray = preferences.getString("paths",null)
        existingArray?.let { existing ->
            val gson = Gson()
            val existingList = gson.fromJson(existing, RecentPathList::class.java)
            existingList.list.map {
                paths.add(it.path)
            }
        }

        val mAdapter =
            DestinationAdapter(
                paths,
                this@DestinationsManagementActivity
            )
        destinationRecyclerView.adapter = mAdapter
        destinationRecyclerView.layoutManager = LinearLayoutManager(this)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(mAdapter))
        itemTouchHelper.attachToRecyclerView(destinationRecyclerView)
    }
}