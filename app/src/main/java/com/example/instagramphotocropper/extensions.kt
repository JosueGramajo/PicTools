package com.example.instagramphotocropper

fun String.remove(vararg characters : String) : String{
    var result = this
    characters.map {
        result = result.replace(it, "")
    }

    return result
}

fun String.removeUnwantedExtension() : String = this.remove(".png", ".jpg", ".gif")