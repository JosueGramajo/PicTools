package com.example.instagramphotocropper.activities

import android.app.AlertDialog
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.example.instagramphotocropper.BuildConfig
import com.example.instagramphotocropper.R
import com.example.instagramphotocropper.databinding.ActivityLoginBinding
import com.example.instagramphotocropper.handlers.AuthenticationHandler

class LoginActivity : BaseActivity() {
    lateinit var binding : ActivityLoginBinding

    var redirectUrl = ""
    var requestUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        redirectUrl = resources.getString(R.string.callback_url)
        requestUrl = "${resources.getString(R.string.base_url)}oauth/authorize?client_id=${BuildConfig.INSTAGRAM_CLIENT_ID}&redirect_uri=$redirectUrl&scope=user_profile,user_media&response_type=code"

        binding.btnLogin.setOnClickListener {
            initDialog()
        }
    }

    private fun initDialog(){
        val alert = AlertDialog.Builder(this).create()
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(requestUrl)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request!!.url.toString()
                if (url.startsWith(redirectUrl)){
                    if (url.contains("code=")){
                        val code = url.substring(url.lastIndexOf("=") + 1, url.lastIndexOf("#_"))
                        alert.dismiss()

                        handleCodeResponse(code)
                    }

                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let {

                }
            }
        }
        alert.setView(webView)
        alert.show()
    }

    private fun handleCodeResponse(code : String){
        showLoader(binding.constraintLayout)

        AuthenticationHandler.getAccessToken(redirectUrl, code, { accessToken ->
            hideLoader(binding.constraintLayout)

            println(accessToken)
        }, {

        })
    }
}