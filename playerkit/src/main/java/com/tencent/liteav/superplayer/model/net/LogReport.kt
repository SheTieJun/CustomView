package com.tencent.liteav.superplayer.model.net

import android.content.*
import com.tencent.liteav.basic.log.TXCLog
import com.tencent.liteav.superplayer.model.net.HttpURLClient.OnHttpCallback
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by liyuejiao on 2018/7/19.
 *
 * 数据上报模块
 */
class LogReport private constructor() {
    private var mAppName: String? = null
    private var mPackageName: String? = null

    private object Holder {
        val instance = LogReport()
    }

    fun uploadLogs(action: String?, usedtime: Long, fileid: Int) {
//        val reqUrl = "https://ilivelog.qcloud.com"
//        var body = ""
//        try {
//            val jsonObject = JSONObject()
//            jsonObject.put("action", action)
//            jsonObject.put("fileid", fileid)
//            jsonObject.put("type", "log")
//            jsonObject.put("bussiness", "superplayer")
//            jsonObject.put("usedtime", usedtime)
//            jsonObject.put("platform", "android")
//            if (mAppName != null) {
//                jsonObject.put("appname", mAppName)
//            }
//            if (mPackageName != null) {
//                jsonObject.put("appidentifier", mPackageName)
//            }
//            body = jsonObject.toString()
//            TXCLog.d(TAG, body)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        HttpURLClient.instance.postJson(reqUrl, body, object : OnHttpCallback {
//            override fun onSuccess(result: String) {}
//            override fun onError() {}
//        })
    }

    fun setAppName(context: Context?) {
        if (context == null) {
            return
        }
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        mAppName =
            if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
                stringId
            )
    }

    fun setPackageName(context: Context?) {
        if (context == null) {
            return
        }
        try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            // 当前版本的包名
            mPackageName = info.packageName
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "TCLogReport"

        //ELK上报事件
        const val ELK_ACTION_CHANGE_RESOLUTION = "change_resolution"
        const val ELK_ACTION_TIMESHIFT = "timeshift"
        const val ELK_ACTION_FLOATMOE = "floatmode"
        const val ELK_ACTION_LIVE_TIME = "superlive"
        const val ELK_ACTION_VOD_TIME = "supervod"
        const val ELK_ACTION_CHANGE_SPEED = "change_speed"
        const val ELK_ACTION_MIRROR = "mirror"
        const val ELK_ACTION_SOFT_DECODE = "soft_decode"
        const val ELK_ACTION_HW_DECODE = "hw_decode"
        const val ELK_ACTION_IMAGE_SPRITE = "image_sprite"
        const val ELK_ACTION_PLAYER_POINT = "player_point"
        val instance: LogReport
            get() = Holder.instance
    }
}