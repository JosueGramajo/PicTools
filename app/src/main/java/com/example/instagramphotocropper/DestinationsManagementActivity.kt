package com.example.instagramphotocropper

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
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

        val mAdapter = DestinationAdapter(paths, this@DestinationsManagementActivity)
        destinationRecyclerView.adapter = mAdapter
        destinationRecyclerView.layoutManager = LinearLayoutManager(this)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(mAdapter))
        itemTouchHelper.attachToRecyclerView(destinationRecyclerView)
    }
}