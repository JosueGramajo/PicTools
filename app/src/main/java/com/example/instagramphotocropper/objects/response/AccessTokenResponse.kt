package com.example.instagramphotocropper.objects.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AccessTokenResponse(
    @SerializedName("access_token")
    @Expose
    var accessToken : String?,

    @SerializedName("user_id")
    @Expose
    var userId : Long?,

    @SerializedName("error_type")
    @Expose
    var errorType : String?,

    @SerializedName("code")
    @Expose
    var code : Int?,

    @SerializedName("error_message")
    @Expose
    var errorMessage : String?
){
    constructor() : this("", 0, "", 0, "")
}