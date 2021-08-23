package com.example.instagramphotocropper.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.instagramphotocropper.*
import com.example.instagramphotocropper.adapters.GalleryAdapter
import com.example.instagramphotocropper.databinding.ResultActivityBinding
import com.example.instagramphotocropper.objects.CustomImage
import com.example.instagramphotocropper.objects.RecentPathList
import com.example.instagramphotocropper.objects.RecentPaths
import com.example.instagramphotocropper.utils.UserDefaultsUtils
import com.example.instagramphotocropper.utils.delete
import com.example.instagramphotocropper.utils.removeUnwantedExtension
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.time.LocalDate
import kotlin.concurrent.thread

class ResultActivity : BaseActivity(){

    lateinit var binding : ResultActivityBinding

    val images = arrayListOf<CustomImage>()

    val outPath = "${Environment.getExternalStorageDirectory()}/InstagramScreenshotCropper/Cropped/"

    val itemOptions = listOf("Remove")

    var pathOptions = arrayListOf<String>()

    lateinit var userDefaultsUtils : UserDefaultsUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.result_activity)

        userDefaultsUtils = UserDefaultsUtils(this)

        binding.recyclerResult.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerResult.adapter =
            GalleryAdapter(images) { imageItem ->
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Select an option")
                alert.setItems(itemOptions.toTypedArray()) { dialog, which ->
                    when(itemOptions[which]){
                        "Remove" -> {
                            imageItem.uri.delete(this)
                            images.removeIf { it.name.equals(imageItem.name) }
                            binding.recyclerResult.adapter!!.notifyDataSetChanged()
                        }
                        else -> {}
                    }
                }
                alert.show()
            }

        binding.fabMove.setOnClickListener {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Select the destination folder")
            alert.setItems(pathOptions.toTypedArray()) { dialog, which ->
                when(pathOptions[which]){
                    "Select new path" -> {
                        openDirectoryPicker()
                    }
                    else -> {
                        writeImagesInSelectedPath(pathOptions[which])
                    }
                }
            }
            alert.show()
        }

        binding.fabDeleteResults.setOnClickListener {
            deleteAllFiles()
        }

        pathOptions = userDefaultsUtils.getPaths()

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

    fun writeImagesInSelectedPath(path : String?){
        val newPath = path!!.replace("/tree/primary:", "${Environment.getExternalStorageDirectory()}/")

        for (image in images){
            try {
                val out = FileOutputStream("${newPath}/${image.name.removeUnwantedExtension()}.png")
                image.image!!.compress(Bitmap.CompressFormat.PNG, 100, out)
            }catch (ex : Exception){

            }
        }

        userDefaultsUtils.savePath(newPath)

        runOnUiThread {
            showAlert("Success", this){
                deleteAllFiles()
                finish()
            }
        }
    }

    fun deleteAllFiles(){
        val file = File(outPath)
        val fileList = file.listFiles()
        for (f in fileList){
            f.delete()
        }

        images.clear()

        binding.recyclerResult.adapter!!.notifyDataSetChanged()
    }

    fun openDirectoryPicker(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivityForResult(Intent.createChooser(intent, "Select destination folder"), 9)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9){
            thread {
                val uri = data!!.data
                val path = uri!!.path
                writeImagesInSelectedPath(path)
            }
        }
    }


    //TODO: LOAD IMAGES

    fun loadImages(){
        images.clear()

        val file = File(outPath)
        val fileList = file.listFiles()
        for (f in fileList){
            val bitmap = BitmapFactory.decodeFile(f.path)
            images.add(
                CustomImage(
                    f.name,
                    Uri.fromFile(f),
                    bitmap
                )
            )
        }

        binding.recyclerResult.adapter!!.notifyDataSetChanged()
    }
}