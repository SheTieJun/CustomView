package com.tencent.liteav.demo.superplayer.model.net

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by hans on 2018/9/11.
 *
 *
 * 超级播放器模块由于涉及查询视频信息，所以需要有一个内置的HTTP请求模块
 *
 *
 * 为了不引入额外的网络请求库，这里使用原生的Java HTTPURLConnection实现
 *
 *
 * 推荐您修改网络模块，使用您项目中的网络请求库，如okHTTP、Volley等
 */
class HttpURLClient {

    companion object   Holder {
        val instance = HttpURLClient()
    }

    /**
     * get请求
     *
     * @param urlStr
     * @param callback
     */
    operator fun get(urlStr: String?, callback: OnHttpCallback?) {
        AsyncTask.execute(Runnable {
            var bufferedReader: BufferedReader? = null
            try {
                val url = URL(urlStr)
                val connection = url.openConnection()
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.connect()
                val `in` = connection.getInputStream()
                if (`in` == null) {
                    callback?.onError()
                    return@Runnable
                }
                bufferedReader = BufferedReader(InputStreamReader(`in`))
                var line: String? = null
                val sb = StringBuilder()
                while (bufferedReader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                callback?.onSuccess(sb.toString())
            } catch (e: IOException) {
                e.printStackTrace()
                callback?.onError()
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    /**
     * post json数据请求
     *
     * @param urlStr
     * @param callback
     */
    fun postJson(urlStr: String?, json: String, callback: OnHttpCallback?) {
        AsyncTask.execute(Runnable {
            var bufferedReader: BufferedReader? = null
            try {
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.requestMethod = "POST"
                connection.addRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.doInput = true
                connection.doOutput = true
                connection.connect()
                val outputStream = connection.outputStream
                outputStream.write(json.toByteArray())
                outputStream.flush()
                outputStream.close()
                val `in` = connection.inputStream
                if (`in` == null) {
                    callback?.onError()
                    return@Runnable
                }
                bufferedReader = BufferedReader(InputStreamReader(`in`))
                var line: String? = null
                val sb = StringBuilder()
                while (bufferedReader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                callback?.onSuccess(sb.toString())
            } catch (e: IOException) {
                e.printStackTrace()
                callback?.onError()
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    interface OnHttpCallback {
        fun onSuccess(result: String)
        fun onError()
    }

}