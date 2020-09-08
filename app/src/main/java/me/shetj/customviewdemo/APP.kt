package me.shetj.customviewdemo

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import me.shetj.base.S

/**
 *
 * <b>@packageName：</b> com.shetj.diyalbume<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2017/12/4<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */
class APP : Application()   {

    override fun onCreate() {
        super.onCreate()
        S.init(this, true,null)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
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