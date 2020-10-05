package com.example.instagramphotocropper.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.instagramphotocropper.R
import com.example.instagramphotocropper.objects.RecentPathList
import com.example.instagramphotocropper.objects.RecentPaths
import com.google.gson.Gson
import java.time.LocalDate

class UserDefaultsUtils(context: Context) {

    val preferences : SharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key),Context.MODE_PRIVATE)

    fun savePath(path : String){
        val usedPath =
            RecentPaths(
                path,
                LocalDate.now()
            )

        val editor = preferences.edit()
        val gson = Gson()

        val existingArray = preferences.getString("paths",null)
        existingArray?.let {

            val existingList = gson.fromJson(it, RecentPathList::class.java)
            if (!(existingList.list.map { it.path }.contains(path))){
                existingList.list.add(usedPath)
            }

            val json = gson.toJson(existingList)

            editor.putString("paths", json)

            editor.apply()

        } ?: run {

            val newList = arrayListOf<RecentPaths>()
            newList.add(usedPath)

            val listObj =
                RecentPathList(newList)

            val json = gson.toJson(listObj)

            editor.putString("paths", json)

            editor.apply()
        }
    }

    fun getPaths() : ArrayList<String>{
        val pathOptions = arrayListOf<String>("Select new path")
        val existingArray = preferences.getString("paths",null)
        existingArray?.let { existing ->
            val gson = Gson()
            val existingList = gson.fromJson(existing, RecentPathList::class.java)
            existingList.list.map {
                pathOptions.add(it.path)
            }
        }
        return pathOptions
    }
}