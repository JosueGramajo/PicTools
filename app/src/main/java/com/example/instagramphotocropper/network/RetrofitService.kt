package com.example.instagramphotocropper.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor


object RetrofitService {
    val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.instagram.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.MINUTES)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build())
            .build()
    }

    inline fun <reified T : Any> createCustomService(url : String) : T?{
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(T::class.java)
    }

    inline fun <reified T : Any> createService() : T? = this.retrofit.create(T::class.java)

}