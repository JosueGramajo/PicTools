package com.example.instagramphotocropper

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.*
import android.provider.LiveFolders.INTENT
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.content.FileProvider.getUriForFile
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*
import java.io.FileDescriptor
import java.io.FileInputStream
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    val MY_PERMISSIONS_REQUEST = 0

    val invalidColors = arrayListOf<PixelColor>()

    lateinit var pd : ProgressDialog

    val inPath = "${Environment.getExternalStorageDirectory()}/InstagramScreenshotCropper/ToBeCropped/"
    val outPath = "${Environment.getExternalStorageDirectory()}/InstagramScreenshotCropper/Cropped/"

    lateinit var handler : Handler

    val images = arrayListOf<CustomImage>()

    val itemOptions = listOf("Remove")

    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.hide()
        fab_add.hide()
        fab_delete.hide()

        verifyFolders()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST)
        }else{
            thread {
                showLoader()

                loadImages()

                handler.sendEmptyMessage(0)
            }
        }

        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                hideLoader()

                if (msg.what == 5){
                    startActivity(Intent(context, ResultActivity::class.java))
                }else{
                    recycler.adapter!!.notifyDataSetChanged()

                    if(images.isEmpty()){
                        recycler.visibility = View.GONE
                        addFilesImageButton.visibility = View.VISIBLE
                        fab.hide()
                        fab_delete.hide()
                        fab_add.hide()
                    }else{
                        recycler.visibility = View.VISIBLE
                        addFilesImageButton.visibility = View.GONE
                        fab.show()
                        fab_delete.show()
                        fab_add.show()
                    }
                }


            }
        }

        addFilesImageButton.setOnClickListener {
            openFilePicker()
        }

        fab.setOnClickListener { view ->
            showLoader()
            thread {
                cropp()

                handler.sendEmptyMessage(5)
            }
        }

        fab_delete.setOnClickListener{ view ->
            alert("Would you like to delete all images?",""){
                yesButton {
                    showLoader()
                    thread {
                        val file = File(inPath)
                        val fileList = file.listFiles()
                        for (f in fileList){
                            f.delete()
                        }

                        images.clear()

                        handler.sendEmptyMessage(0)
                    }
                }
                noButton {  }
            }.show()
        }

        fab_add.setOnClickListener{
            openFilePicker()
        }

        recycler.layoutManager = GridLayoutManager(this,3)
        recycler.adapter = GalleryAdapter(images){ imageItem ->
            selector("Select an option", itemOptions) { dialogInterface, i ->
                when(itemOptions[i]){
                    "Remove" -> {
                        val file = File(imageItem.path)
                        file.delete()

                        images.removeIf { it.name.equals(imageItem.name) }

                        recycler.adapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
        recycler.visibility = View.GONE
    }

    fun openFilePicker(){
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        startActivityForResult(Intent.createChooser(intent, "Select files"), 123)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK){
            showLoader()

            thread {
                data?.let {
                    if (null != it.clipData) {
                        for (i in 0 until it.clipData!!.itemCount) {
                            val uri = it.clipData!!.getItemAt(i).uri

                            val name = getImageName(uri)

                            copyBitmapFromUri(uri, name)
                        }
                    } else {
                        val uri = data.data

                        val name = getImageName(uri)

                        copyBitmapFromUri(uri, name)
                    }

                    loadImages()

                    handler.sendEmptyMessage(0)
                }
            }
        }
    }

    fun getImageName(uri: Uri) : String{

        val cursor: Cursor? = contentResolver.query( uri, null, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }

        return "desconocido"
    }

    private fun copyBitmapFromUri(uri: Uri, name: String) {
        val parcelFileDescriptor: ParcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")!!
        val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
        val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()

        val out = FileOutputStream("${inPath}${name}")
        image.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    fun verifyFolders() {
        val input = File(inPath)
        val output = File(outPath)
        if (!input.exists()){
            input.mkdirs()
        }
        if (!output.exists()){
            output.mkdirs()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                val dir = getExternalFilesDir("file://${Environment.getExternalStorageDirectory()}/InstagramScreenshotCropper/")
                val intent = Intent(Intent.ACTION_VIEW)
                val mydir = getUriForFile(this, "com.example.instagramphotocropper.fileprovider", dir)
                intent.setDataAndType(mydir, "resource/folder")
                startActivity(intent);
            }

            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    fun showLoader(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        pd = ProgressDialog.show(this, "", "Loading")
    }

    fun hideLoader(){
        pd.dismiss()

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    fun cropp(){

        for (i in images){

            var yToStart = 0
            var yToFinish = 0

            var mode = "topLine"

            heightLoop@ for (y in 1..(i.image.height - 1)){
                var whiteNumber = 0

                invalidColors.clear()

                for (x in 1..(i.image.width - 1)){
                    val pixel = i.image.getPixel(x, y)

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

                for (invalid in invalidColors){
                    if (invalid.amount > 200){
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


            val resizedbitmap1 = Bitmap.createBitmap(i.image, 0, yToStart, 1080, yToFinish - yToStart);
            try {
                val out = FileOutputStream("${outPath}${i.name}.png")
                resizedbitmap1.compress(Bitmap.CompressFormat.PNG, 100, out)
            }catch (ex : Exception){

            }
        }
    }

    fun loadImages(){
        images.clear()

        val bitmapList = arrayListOf<Bitmap>()
        val file = File(inPath)
        val fileList = file.listFiles()
        for (f in fileList){
            bitmapList.add(BitmapFactory.decodeFile(f.path))
            images.add(CustomImage(f.name, f.path, BitmapFactory.decodeFile(f.path)))
        }
    }
}

class GenericFileProvider : FileProvider() {}
