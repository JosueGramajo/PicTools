package com.example.instagramphotocropper

import android.app.ProgressDialog
import android.content.Intent
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
import kotlin.concurrent.thread

class ResultActivity : AppCompatActivity(){

    val images = arrayListOf<CustomImage>()

    val outPath = "${Environment.getExternalStorageDirectory()}/InstagramScreenshotCropper/Cropped/"

    val itemOptions = listOf("Remove")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)

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
            openDirectoryPicker()
        }

        loadImages()
    }

    override fun onBackPressed() {
        finish()
    }

    fun writeImagesInSelectedPath(path : String){
        val newPath = path.replace("/tree/primary:", "${Environment.getExternalStorageDirectory()}/")

        for (image in images){
            try {
                val out = FileOutputStream("${newPath}/${image.name}.png")
                image.image.compress(Bitmap.CompressFormat.PNG, 100, out)
            }catch (ex : Exception){

            }
        }

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