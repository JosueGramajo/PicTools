package com.example.instagramphotocropper.handlers

import com.example.instagramphotocropper.BuildConfig
import com.example.instagramphotocropper.network.RetrofitService
import com.example.instagramphotocropper.network.api.InstagramApi
import com.example.instagramphotocropper.objects.response.AccessTokenResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MultipartBody

object AuthenticationHandler {
    fun getAccessToken(redirectUrl : String, code : String, onSuccess : (String) -> Unit, onFailure : () -> Unit){

        val request = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("client_id", BuildConfig.INSTAGRAM_CLIENT_ID)
            .addFormDataPart("client_secret", BuildConfig.INSTAGRAM_CLIENT_SECRET)
            .addFormDataPart("grant_type", "authorization_code")
            .addFormDataPart("redirect_uri", redirectUrl)
            .addFormDataPart("code", code)
            .build()

        val client = RetrofitService.createService<InstagramApi>()!!
        client.getAccessToken(request).enqueue(object : Callback<AccessTokenResponse>{
            override fun onResponse(call: Call<AccessTokenResponse>, response: Response<AccessTokenResponse>) {
                if (response.isSuccessful){
                    val resp = response.body()!!
                    val accessToken = resp.accessToken
                    onSuccess(accessToken!!)
                }else{
                    val errorBody = response.errorBody()
                    onFailure()
                }
            }

            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                onFailure()
            }
        })
    }
}