package com.example.instagramphotocropper

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import java.time.LocalDate
import java.util.*


data class PixelColor(var color : Int, var amount : Int)

data class CustomImage(var name : String, var path : String, var image : Bitmap)

data class RecentPaths(var path : String, var date : LocalDate)

data class RecentPathList(var list : ArrayList<RecentPaths>)