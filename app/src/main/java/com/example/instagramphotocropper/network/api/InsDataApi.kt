package com.example.instagramphotocropper.network.api

import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Url

interface InsDataApi {
    @Headers("Authorization: Bearer IGQVJVckc2a25GOUpTcy1GenBHM0hFZAkd5Q1IwVGJVZA1ByZAnhLQndQV040Nng1ZA3pYdzIwNkRrRWlHVmZAtVE9ZAdGhJWUpOQTlrSU5yQU1xRi0zYzNvbWdaVnZAzSXBzZA1RUYUpXZA2NMejFXM3gwUHA2RWc5cS1sS1ZAYcGg4")
    @GET
    fun get(@Url url : HttpUrl) : Call<ResponseBody>
}