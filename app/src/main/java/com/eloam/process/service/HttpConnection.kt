package com.eloam.process.service

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class HttpConnection {

    fun http() {
        val okHttpClient = OkHttpClient()
        val postBody =
            FormBody.Builder().apply {
                add("name", "name")
                add("user", "user")
                add("password", "password")
            }.build()
        val request = Request.Builder().post(postBody).url("url").build()
        val newCall = okHttpClient.newCall(request)
        newCall.execute()
    }
}