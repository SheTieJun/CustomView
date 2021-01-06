package me.shetj.customviewdemo

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.content.Context
import com.tencent.liteav.superplayer.model.net.HttpClient
import com.tencent.liteav.superplayer.model.net.HttpURLClient
import me.shetj.base.S
import me.shetj.base.network.RxHttp
import me.shetj.base.network.callBack.NetCallBack
import me.shetj.base.network.callBack.SimpleNetCallBack
import me.shetj.customviewdemo.utils.UncaughtExceptionHandler

/**
 *
 * <b>@packageName：</b> com.shetj.diyalbume<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2017/12/4<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */
class APP : Application() {

    override fun onCreate() {
        super.onCreate()
        S.init(this, true, null)
        HttpURLClient.instance.setHttpClient(object : HttpClient {
            override fun doGet(url: String, callBack: HttpURLClient.OnHttpCallback?) {
                RxHttp.get(url).executeCus(object : SimpleNetCallBack<String>(this@APP) {
                    override fun onSuccess(data: String) {
                        super.onSuccess(data)
                        callBack?.onSuccess(data)
                    }

                    override fun onError(e: Exception) {
                        super.onError(e)
                        callBack?.onError()
                    }
                })
            }

            override fun doPost(url: String, json: String, callBack: HttpURLClient.OnHttpCallback?) {
                RxHttp.post(url).apply {
                    upJson(json)
                }.executeCus(object : SimpleNetCallBack<String>(this@APP) {
                    override fun onSuccess(data: String) {
                        super.onSuccess(data)
                        callBack?.onSuccess(data)
                    }

                    override fun onError(e: Exception) {
                        super.onError(e)
                        callBack?.onError()
                    }
                })
            }
        })
//        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    fun isAppForeground(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = manager.runningAppProcesses
        if (info == null || info.size == 0) {
            return false
        }
        for (aInfo in info) {
            if (aInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return aInfo.processName == packageName
            }
        }
        return false
    }

}