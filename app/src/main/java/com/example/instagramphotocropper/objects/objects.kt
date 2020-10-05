package com.example.instagramphotocropper.objects

import android.graphics.Bitmap
import java.time.LocalDate
import java.util.*


data class PixelColor(var color : Int, var amount : Int)

data class CustomImage(var name : String, var path : String, var image : Bitmap)

data class RecentPaths(var path : String, var date : LocalDate)

data class RecentPathList(var list : ArrayList<RecentPaths>)