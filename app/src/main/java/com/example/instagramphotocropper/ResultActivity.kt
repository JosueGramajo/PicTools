package com.example.instagramphotocropper

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.DocumentsContract
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Gallery
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.result_activity.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.time.LocalDate
import kotlin.concurrent.thread

class ResultActivity : AppCompatActivity(){

    val images = arrayListOf<CustomImage>()

    val outPath = "${Environment.getExternalStorageDirectory()}/InstagramScreenshotCropper/Cropped/"

    val itemOptions = listOf("Remove")

    val pathOptions = arrayListOf<String>("Select new path")

    lateinit  var preferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)

        preferences = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE)

        recyclerResult.layoutManager = GridLayoutManager(this, 3)
        recyclerResult.adapter = GalleryAdapter(images){ imageItem ->
            selector("Select an option", itemOptions) { dialogInterface, i ->
                when(itemOptions[i]){
                    "Remove" -> {
                        val file = File(imageItem.path)
                        file.delete()

                        images.removeIf { it.name.equals(imageItem.name) }

                        recyclerResult.adapter!!.notifyDataSetChanged()
                    }
                }
            }
        }

        fab_move.setOnClickListener {
            selector("Select the destination folder", pathOptions) { dialogInterface, i ->
                when(pathOptions[i]){
                    "Select new path" -> {
                        openDirectoryPicker()
                    }
                    else -> {
                        writeImagesInSelectedPath(pathOptions[i])
                    }
                }
            }

        }

        fab_delete_results.setOnClickListener {
            deleteAllFiles()
        }

        val existingArray = preferences.getString("paths",null)
        existingArray?.let { existing ->
            val gson = Gson()
            val existingList = gson.fromJson(existing, RecentPathList::class.java)
            existingList.list.map {
                pathOptions.add(it.path)
            }
        }

        loadImages()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        val file = File(outPath)
        val fileList = file.listFiles()
        for (f in fileList){
            f.delete()
        }

        super.onDestroy()
    }

    fun writeImagesInSelectedPath(path : String){
        val newPath = path.replace("/tree/primary:", "${Environment.getExternalStorageDirectory()}/")

        for (image in images){
            try {
                val out = FileOutputStream("${newPath}/${image.name.removeUnwantedExtension()}.png")
                image.image.compress(Bitmap.CompressFormat.PNG, 100, out)
            }catch (ex : Exception){

            }
        }

        saveUsedPath(newPath)

        alert("Success") {
            yesButton {
                deleteAllFiles()
                finish()
            }
        }.show()
    }

    fun saveUsedPath(path : String){
        val usedPath = RecentPaths(path, LocalDate.now())

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

            val listObj = RecentPathList(newList)

            val json = gson.toJson(listObj)

            editor.putString("paths", json)

            editor.apply()
        }
    }

    fun deleteAllFiles(){
        val file = File(outPath)
        val fileList = file.listFiles()
        for (f in fileList){
            f.delete()
        }

        images.clear()

        recyclerResult.adapter!!.notifyDataSetChanged()
    }

    fun openDirectoryPicker(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivityForResult(Intent.createChooser(intent, "Select destination folder"), 9)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9){
            val uri = data!!.data
            val path = uri.path

            writeImagesInSelectedPath(path)
        }
    }

    fun loadImages(){
        images.clear()

        val bitmapList = arrayListOf<Bitmap>()
        val file = File(outPath)
        val fileList = file.listFiles()
        for (f in fileList){
            bitmapList.add(BitmapFactory.decodeFile(f.path))
            images.add(CustomImage(f.name, f.path, BitmapFactory.decodeFile(f.path)))
        }

        recyclerResult.adapter!!.notifyDataSetChanged()
    }
}