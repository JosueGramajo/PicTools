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
    val json = "https://josuegramajo.com/encriptacion?code=AQDsIJ-7oeQyS5tg9z4KT9-iyEMHAbTpwcF3nAxZ88meciuA_MmO0IvQSsgpyW4v4KnJ2pM5hr9tUS_-9JGE4xVn94aWZUnhx8AgebU99E6hbVuWzR0AVVlGyPoY4Hmha-0_4ub6GsM0AYuRGp54G6jAp1kEbA57fh5oPgBZzRhfCUYO4Tiz-8uryhnWhvTlBcKhUCiSMVPbaH5iJRMQPj0R-rCEDeIt3xKdlgiepYrklQ#_"

    val res = json.substring(json.lastIndexOf("=") + 1, json.lastIndexOf("#_"))

    println(res)


}