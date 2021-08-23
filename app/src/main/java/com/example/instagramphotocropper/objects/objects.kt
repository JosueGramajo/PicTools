package com.example.instagramphotocropper.objects

import android.graphics.Bitmap
import android.net.Uri
import java.time.LocalDate
import java.util.*


data class PixelColor(var color : Int, var amount : Int)

data class CustomImage(var name : String, var uri : Uri, var image : Bitmap)

data class RecentPaths(var path : String, var date : LocalDate)

data class RecentPathList(var list : ArrayList<RecentPaths>)