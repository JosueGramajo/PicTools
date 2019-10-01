package com.example.instagramphotocropper

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import android.widget.RelativeLayout
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    val MY_PERMISSIONS_REQUEST = 0

    val invalidColors = arrayListOf<PixelColor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST)
        }

        fab.setOnClickListener { view ->
            cropp()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showLoader(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        progressBar.setVisibility(View.VISIBLE)
    }

    fun hideLoader(){
        progressBar.setVisibility(View.GONE)

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    fun cropp(){
        showLoader()

        val list = loadImages()

        var counter = 0

        for (image in list){

            var yToStart = 0
            var yToFinish = 0

            var mode = "topLine"

            counter += 1

            heightLoop@ for (y in 1..(image.height - 1)){
                var whiteNumber = 0

                invalidColors.clear()

                for (x in 1..(image.width - 1)){
                    //println("${x},${y}")
                    val pixel = image.getPixel(x, y)

                    val red = Color.red(pixel)
                    val blue = Color.blue(pixel)
                    val green = Color.green(pixel)

                    if (red == 255 && blue == 255 && green == 255){
                        whiteNumber += 1
                    }else{
                        if (red == blue && blue == green){

                            invalidColors.firstOrNull { it.color == red }?.let {
                                it.amount += 1
                            } ?: kotlin.run {
                                invalidColors.add(PixelColor(red, 1))
                            }
                        }
                    }
                }

                for (i in invalidColors){
                    if (i.amount > 200){
                        continue@heightLoop
                    }
                }

                if (whiteNumber < 200 && mode.equals("topLine")){
                    mode = "middle"
                    yToStart = y
                }

                if (whiteNumber > 1000 && mode.equals("middle")){
                    yToFinish = y

                    break@heightLoop
                }
            }


            val resizedbitmap1 = Bitmap.createBitmap(image, 0, yToStart, 1080, yToFinish - yToStart);
            try {
                val out = FileOutputStream("${Environment.getExternalStorageDirectory()}/Cropped/${counter}.png")
                resizedbitmap1.compress(Bitmap.CompressFormat.PNG, 100, out)
            }catch (ex : Exception){

            }
        }

        hideLoader()
    }

    fun loadImages() : ArrayList<Bitmap>{
        val d = Environment.getExternalStorageDirectory().toString()

        val bitmapList = arrayListOf<Bitmap>()
        val file = File("${Environment.getExternalStorageDirectory()}/ToBeCropped/")
        val fileList = file.listFiles()
        for (f in fileList){
            bitmapList.add(BitmapFactory.decodeFile(f.path))
        }

        return bitmapList;
    }
}
