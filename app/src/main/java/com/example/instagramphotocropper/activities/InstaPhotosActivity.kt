package com.example.instagramphotocropper.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.instagramphotocropper.R
import com.example.instagramphotocropper.adapters.InstaGalleryAdapter
import com.example.instagramphotocropper.databinding.ActivityInstaPhotosBinding
import com.example.instagramphotocropper.handlers.HtmlDataHandler
import com.example.instagramphotocropper.network.RetrofitService
import com.example.instagramphotocropper.network.api.InsDataApi
import com.example.instagramphotocropper.objects.InstagramData
import com.example.instagramphotocropper.utils.GridItemDecoration
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InstaPhotosActivity : AppCompatActivity() {

    lateinit var binding : ActivityInstaPhotosBinding

    var imageList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_insta_photos)

        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent) // Handle text being sent
                } else if (intent.type?.startsWith("image/") == true) {
                    handleSendImage(intent) // Handle single image being sent
                }
            }
            intent?.action == Intent.ACTION_SEND_MULTIPLE
                    && intent.type?.startsWith("image/") == true -> {
                handleSendMultipleImages(intent) // Handle multiple images being sent
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }

        binding.recycler.layoutManager = GridLayoutManager(this, 3)
        binding.recycler.addItemDecoration(GridItemDecoration(2))
        binding.recycler.adapter = InstaGalleryAdapter(imageList, this, {})

        binding.testButton.setOnClickListener {

        }
    }

    private fun loadImages(url : String){
        val client = RetrofitService.createService<InsDataApi>(url)!!
        client.get(HttpUrl.parse(url)!!).enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()!!.string()
                val json = HtmlDataHandler.getJson(body)
                val obj = Gson().fromJson(json, InstagramData::class.java)
                val urls = HtmlDataHandler.getUrls(obj)

                imageList.clear()
                urls.forEach {
                    imageList.add(it)
                }

                binding.recycler.adapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            loadImages(it)
        }
    }

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            // Update UI to reflect image being shared
        }
    }

    private fun handleSendMultipleImages(intent: Intent) {
        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
            // Update UI to reflect multiple images being shared
        }
    }
}