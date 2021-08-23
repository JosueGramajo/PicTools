package com.example.instagramphotocropper.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.instagramphotocropper.*
import com.example.instagramphotocropper.adapters.GalleryAdapter
import com.example.instagramphotocropper.objects.CustomImage
import com.example.instagramphotocropper.objects.RecentPathList
import com.example.instagramphotocropper.objects.RecentPaths
import com.example.instagramphotocropper.utils.UserDefaultsUtils
import com.example.instagramphotocropper.utils.removeUnwantedExtension
import com.google.gson.Gson
import kotlinx.android.synthetic.main.result_activity.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.selector
import org.jetbrains.anko.yesButton
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.time.LocalDate

class ResultActivity : AppCompatActivity(){

    val images = arrayListOf<CustomImage>()

    val outPath = "${Environment.getExternalStorageDirectory()}/InstagramScreenshotCropper/Cropped/"

    val itemOptions = listOf("Remove")

    var pathOptions = arrayListOf<String>()

    lateinit var userDefaultsUtils : UserDefaultsUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)

        userDefaultsUtils = UserDefaultsUtils(this)

        recyclerResult.layoutManager = GridLayoutManager(this, 3)
        recyclerResult.adapter =
            GalleryAdapter(images) { imageItem ->
                selector("Select an option", itemOptions) { dialogInterface, i ->
                    when (itemOptions[i]) {
                        "Remove" -> {
                            //TODO: DELETE WITH URI

                            /*
                            val file = File(imageItem.path)
                            file.delete()

                            images.removeIf { it.name.equals(imageItem.name) }

                            recyclerResult.adapter!!.notifyDataSetChanged()
                             */
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

        pathOptions = userDefaultsUtils.getPaths()

        //loadImages()
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

    fun writeImagesInSelectedPath(path : String?){
        val newPath = path!!.replace("/tree/primary:", "${Environment.getExternalStorageDirectory()}/")

        for (image in images){
            try {
                val out = FileOutputStream("${newPath}/${image.name.removeUnwantedExtension()}.png")
                image.image.compress(Bitmap.CompressFormat.PNG, 100, out)
            }catch (ex : Exception){

            }
        }

        userDefaultsUtils.savePath(newPath)

        alert("Success") {
            yesButton {
                deleteAllFiles()
                finish()
            }
        }.show()
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
            val path = uri!!.path

            writeImagesInSelectedPath(path)
        }
    }


    //TODO: LOAD IMAGES
    /*
    fun loadImages(){
        images.clear()

        val bitmapList = arrayListOf<Bitmap>()
        val file = File(outPath)
        val fileList = file.listFiles()
        for (f in fileList){
            bitmapList.add(BitmapFactory.decodeFile(f.path))
            images.add(
                CustomImage(
                    f.name,
                    f.path,
                    BitmapFactory.decodeFile(f.path)
                )
            )
        }

        recyclerResult.adapter!!.notifyDataSetChanged()
    }

     */
}