package com.example.instagramphotocropper.network.api

import com.example.instagramphotocropper.objects.request.AccessTokenRequest
import com.example.instagramphotocropper.objects.response.AccessTokenResponse
import okhttp3.HttpUrl
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface InstagramApi {
    @Headers("Authorization: Bearer IGQVJVckc2a25GOUpTcy1GenBHM0hFZAkd5Q1IwVGJVZA1ByZAnhLQndQV040Nng1ZA3pYdzIwNkRrRWlHVmZAtVE9ZAdGhJWUpOQTlrSU5yQU1xRi0zYzNvbWdaVnZAzSXBzZA1RUYUpXZA2NMejFXM3gwUHA2RWc5cS1sS1ZAYcGg4")
    @GET
    fun get(@Url url : HttpUrl) : Call<ResponseBody>

    @POST("/oauth/access_token")
    fun getAccessToken(@Body request : RequestBody) : Call<AccessTokenResponse>
}