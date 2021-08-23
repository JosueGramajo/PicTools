package com.example.instagramphotocropper.activities

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showAlert(text : String, context : Context, onAccept : () -> Unit){
        val alert = AlertDialog.Builder(context)
        alert.setMessage(text)
        alert.setPositiveButton("Aceptar"){ dialog, which ->
            onAccept()
        }
        alert.show()
    }
}