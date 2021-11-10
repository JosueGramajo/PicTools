package com.example.instagramphotocropper.objects.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AccessTokenRequest(
    @SerializedName("client_id")
    @Expose
    val clientId : String,

    @SerializedName("client_secret")
    @Expose
    val clientSecret : String,

    @SerializedName("grant_type")
    @Expose
    val grantType : String,

    @SerializedName("redirect_uri")
    @Expose
    val redirectUri : String,

    @SerializedName("code")
    @Expose
    val code : String
){
    constructor() : this("", "", "", "", "")

    constructor(clientId : String, clientSecret: String, redirectUri: String, code: String) : this(clientId, clientSecret, "authorization_code", redirectUri, code)
}
