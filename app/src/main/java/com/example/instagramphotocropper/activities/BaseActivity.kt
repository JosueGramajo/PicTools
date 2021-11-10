package com.example.instagramphotocropper.activities

import android.R
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

open class BaseActivity : AppCompatActivity() {

    lateinit var alert : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showLoader(rootView : ConstraintLayout){
        alert = AlertDialog.Builder(this).create()

        val progressBar = ProgressBar(this)
        val progressBarParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        progressBarParams.gravity = Gravity.CENTER

        progressBar.layoutParams = progressBarParams

        alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.setView(progressBar)
        alert.show()
    }

    fun hideLoader(rootView: ConstraintLayout){
        alert?.let {
            alert.dismiss()
        }
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