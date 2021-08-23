package com.example.instagramphotocropper

import com.example.instagramphotocropper.objects.CustomImage
import com.example.instagramphotocropper.objects.ImageData
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.*
import kotlinx.serialization.json.*


fun main(){
    val json = "[{\"name\":\"IMG_20210823_121343.jpg\",\"uri\":\"content://com.android.providers.media.documents/document/image%3A464244\"},{\"name\":\"IMG_20210823_121344_1.jpg\",\"uri\":\"content://com.android.providers.media.documents/document/image%3A464246\"},{\"name\":\"IMG_20210823_121344.jpg\",\"uri\":\"content://com.android.providers.media.documents/document/image%3A464245\"}]"

    val gson = GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create()

    val listJackson = ObjectMapper().readValue(json, Array<ImageData>::class.java)
    val listGson = Gson().fromJson(json, Array<ImageData>::class.java)
    val listKtlSerializer = Json.decodeFromString<Array<ImageData>>(json)



    print("")


}