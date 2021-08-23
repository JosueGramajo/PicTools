package com.example.instagramphotocropper.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toFile
import java.io.File

fun String.remove(vararg characters : String) : String{
    var result = this
    characters.map {
        result = result.replace(it, "")
    }

    return result
}

fun String.removeUnwantedExtension() : String = this.remove(".png", ".jpg", ".gif")

fun Uri.delete(context: Context){
    val realPath = RealPathUtil.getRealPath(context, this)
    val originalFile = File(realPath)
    if (originalFile.exists()){
        originalFile.delete()
    }
}
