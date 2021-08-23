package com.example.instagramphotocropper.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.instagramphotocropper.*
import com.example.instagramphotocropper.adapters.GalleryAdapter
import com.example.instagramphotocropper.databinding.ActivityMainBinding
import com.example.instagramphotocropper.objects.CustomImage
import com.example.instagramphotocropper.objects.PixelColor
import com.example.instagramphotocropper.utils.RealPathUtil
import com.example.instagramphotocropper.utils.UserDefaultsUtils
import com.example.instagramphotocropper.utils.removeUnwantedExtension
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.io.FileDescriptor
import kotlin.concurrent.thread
import com.google.gson.GsonBuilder

class MainActivity : BaseActivity() {

    lateinit var binding : ActivityMainBinding

    val MY_PERMISSIONS_REQUEST = 0

    val invalidColors = arrayListOf<PixelColor>()

    lateinit var pd : ProgressDialog

    val inPath = "${Environment.getExternalStorageDirectory()}/InstagramScreenshotCropper/ToBeCropped/"
    val outPath = "${Environment.getExternalStorageDirectory()}/InstagramScreenshotCropper/Cropped/"

    val screenshotsPath = "${Environment.getExternalStorageDirectory()}/Pictures/Screenshots/"

    lateinit var handler : Handler

    val images = arrayListOf<CustomImage>()

    val itemOptions = listOf("Remove")

    val context = this

    companion object ImageUtils{
        var originNames = arrayListOf<String>()
    }

    var pathOptions = arrayListOf<String>()

    lateinit var userDefaultsUtils : UserDefaultsUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)

        binding.fabCut.hide()
        binding.fabAdd.hide()
        binding.fabDelete.hide()
        binding.fabMovePrior.hide()

        verifyFolders()

        userDefaultsUtils = UserDefaultsUtils(this.context)

        pathOptions = userDefaultsUtils.getPaths()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST)
        }else{
            thread {
                showLoader()

                //TODO: Do this async
                //loadImages()

                handler.sendEmptyMessage(0)
            }
        }

        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                hideLoader()

                if (msg.what == 5){
                    startActivity(Intent(context, ResultActivity::class.java))
                }else{
                    binding.recycler.adapter!!.notifyDataSetChanged()

                    if(images.isEmpty()){
                        binding.recycler.visibility = View.GONE
                        binding.addFilesImageButton.visibility = View.VISIBLE
                        binding.fabCut.hide()
                        binding.fabDelete.hide()
                        binding.fabAdd.hide()
                        binding.fabMovePrior.hide()
                    }else{
                        binding.recycler.visibility = View.VISIBLE
                        binding.addFilesImageButton.visibility = View.GONE
                        binding.fabCut.show()
                        binding.fabDelete.show()
                        binding.fabAdd.show()
                        binding.fabMovePrior.show()
                    }
                }


            }
        }

        binding.addFilesImageButton.setOnClickListener {
            openFilePicker()
        }

        binding.fabCut.setOnClickListener { view ->
            showLoader()
            thread {
                for (i in images){
                    cropp(i)
                }

                handler.sendEmptyMessage(5)
            }
        }

        binding.fabDelete.setOnClickListener{ view ->
            val alert = AlertDialog.Builder(this)
            alert.setMessage("Would you like to delete all images?")
            alert.setPositiveButton("Aceptar") { dialog, which ->
                showLoader()
                thread {
                    val file = File(inPath)
                    val fileList = file.listFiles()
                    for (f in fileList){
                        f.delete()
                    }

                    images.clear()

                    originNames.map { name ->
                        val oFile = File("$screenshotsPath$name")
                        deleteImage(oFile)
                    }
                    originNames.clear()


                    handler.sendEmptyMessage(0)
                }
            }
            alert.setNegativeButton("Cancelar"){ dialog, which -> }

            alert.show()
        }

        binding.fabAdd.setOnClickListener{
            openFilePicker()
        }

        binding.fabMovePrior.setOnClickListener {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Select the destination folder")
            alert.setItems(pathOptions.toTypedArray()) { dialog, which ->
                when(pathOptions[which]){
                    "Select new path" -> {
                        openDirectoryPicker()
                    }
                    else -> {
                        writeImagesInSelectedPath(pathOptions[which])
                    }
                }
            }
            alert.show()
        }

        binding.recycler.layoutManager = GridLayoutManager(this,3)
        binding.recycler.adapter =
            GalleryAdapter(images) { imageItem ->

                val alert = AlertDialog.Builder(this)
                alert.setTitle("Select an option")
                alert.setItems(itemOptions.toTypedArray()) { dialog, which ->
                    when(itemOptions[which]){
                        "Remove" -> {
                            deleteFileInUri(imageItem.uri)

                            images.removeIf { it.name == imageItem.name }

                            handler.sendEmptyMessage(0)
                        }
                        else -> {}
                    }
                }
                alert.show()
            }
        binding.recycler.visibility = View.GONE
    }

    fun deleteImage(image : File){
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DATA} = ?"
        val selectionArgs = arrayOf(image.absolutePath)
        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentResolver = contentResolver

        val cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null)
        cursor?.let {
            if (cursor.moveToFirst()){
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                contentResolver.delete(deleteUri, null, null)
            }
            cursor.close()
        }
    }

    fun openDirectoryPicker(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivityForResult(Intent.createChooser(intent, "Select destination folder"), 9)
    }

    fun writeImagesInSelectedPath(path : String?){
        val newPath = path!!.replace("/tree/primary:", "${Environment.getExternalStorageDirectory()}/")

        for (image in images){
            try {
                val out = FileOutputStream("${newPath}/${image.name.removeUnwantedExtension()}.png")
                image.image!!.compress(Bitmap.CompressFormat.PNG, 100, out)
            }catch (ex : Exception){

            }
        }

        userDefaultsUtils.savePath(newPath)

        showAlert("Success", this){}
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
                            thread {
                                val uri = it.clipData!!.getItemAt(i).uri

                                //TODO:REMOVE THIS
                                val name = getImageName(uri)


                                loadImage(uri)

                                /*val name = getImageName(uri)

                                copyBitmapFromUri(uri, name)*/

                                originNames.add(name)

                                handler.sendEmptyMessage(0)

                                if (i == (it.clipData!!.itemCount - 1)){
                                    //Last item
                                    saveListToSharedPreferences()
                                }
                            }
                        }
                    } else {
                        val uri = data.data

                        loadImage(uri)

                        //TODO: REMOVE THIS
                        val name = getImageName(uri)

                        //copyBitmapFromUri(uri, name)

                        originNames.add(name)

                        handler.sendEmptyMessage(0)
                    }



                    //loadImages()


                }
            }
        }else if (requestCode == 9){
            val uri = data!!.data
            val path = uri!!.path

            writeImagesInSelectedPath(path)
        }
    }

    private fun getImageName(uri: Uri?) : String{
        uri?.let {
            val cursor: Cursor? = contentResolver.query( uri, null, null, null, null, null)

            cursor?.use {
                if (it.moveToFirst()) {
                    return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
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

        val realPath = RealPathUtil.getRealPath(this, uri)
        val originalFile = File(realPath)
        if (originalFile.exists()){
            originalFile.delete()
        }
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
                /*val dir = getExternalFilesDir("file://${Environment.getExternalStorageDirectory()}/InstagramScreenshotCropper/")
                val intent = Intent(Intent.ACTION_VIEW)
                val mydir = getUriForFile(this, "com.example.instagramphotocropper.fileprovider", dir)
                intent.setDataAndType(mydir, "resource/folder")
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

                this.startActivity(Intent.createChooser(intent, "Open folder"));*/

                startActivity(Intent(context, DestinationsManagementActivity::class.java))

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

    fun cropp(img : CustomImage){
        try {
            var yToStart = 0
            var yToFinish = 0

            var mode = "topLine"

            heightLoop@ for (y in 1..(img.image!!.height - 1)){
                var whiteNumber = 0

                invalidColors.clear()

                for (x in 1 until img.image!!.width){
                    val pixel = img.image!!.getPixel(x, y)

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
                                invalidColors.add(
                                    PixelColor(
                                        red,
                                        1
                                    )
                                )
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

                if (whiteNumber > (img.image!!.width * 0.99) && mode.equals("middle")){
                    yToFinish = y

                    break@heightLoop
                }
            }


            val resizedbitmap1 = Bitmap.createBitmap(img.image!!, 0, yToStart, 1080, yToFinish - yToStart);

            val out = FileOutputStream("${outPath}${img.name.removeUnwantedExtension()}.png")
            resizedbitmap1.compress(Bitmap.CompressFormat.PNG, 100, out)
        }catch (ex : Exception){
            //TODO:
        }

    }

    fun loadImage(uri : Uri?){
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val name = getImageName(uri)
            images.add(
                CustomImage(
                    name,
                    uri,
                    bitmap
                )
            )
        }
    }

    fun deleteFileInUri(uri : Uri){
        val fileToDelete = File(uri.path)
        if (fileToDelete.exists()){
            fileToDelete.delete()
        }
    }

    private fun saveListToSharedPreferences(){
        val gson = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

        val mockList = gson.toJson(images)

        print("")
    }

    /*fun loadImages(){
        images.clear()

        val bitmapList = arrayListOf<Bitmap>()
        val file = File(inPath)
        val fileList = file.listFiles()
        for (f in fileList){
            bitmapList.add(BitmapFactory.decodeFile(f.path))
            images.add(
                CustomImage(
                    f.name,
                    f.path,
                    BitmapFactory.decodeFile(f.path)
                )
            )
        }
    }*/
}

class GenericFileProvider : FileProvider() {}