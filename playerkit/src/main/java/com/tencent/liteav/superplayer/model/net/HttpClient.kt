package com.tencent.liteav.superplayer.model.net

interface HttpClient {
    fun doGet(url: String, callBack: HttpURLClient.OnHttpCallback?)

    fun doPost(url: String, json: String, callBack: HttpURLClient.OnHttpCallback?)
}