package me.shetj.custom.floatview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import java.util.*
import kotlin.collections.ArrayList

/**
 * 用于在内部自动申请权限
 * https://github.com/yhaolpz
 */
class FloatActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 23) {
            requestAlertWindowPermission()
        }
    }

    @RequiresApi(api = 23)
    private fun requestAlertWindowPermission() {
        val intent = Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION")
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, 1)
    }

    @RequiresApi(api = 23)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Build.VERSION.SDK_INT >= 23) {
            if (Util.hasPermission(this)) {
                mPermissionListener!!.onSuccess()
            } else {
                mPermissionListener!!.onFail()
            }
        }
        finish()
    }

    companion object {
        private var mPermissionListenerList: ArrayList<PermissionListener>? =
            null

        @JvmStatic
        @Synchronized
        fun request(
            context: Context,
            permissionListener: PermissionListener
        ) {
            if (mPermissionListenerList == null) {
                mPermissionListenerList = ArrayList()
                mPermissionListener = object : PermissionListener {
                    override fun onSuccess() {
                        for (listener in mPermissionListenerList!!) {
                            listener.onSuccess()
                        }
                    }

                    override fun onFail() {
                        for (listener in mPermissionListenerList!!) {
                            listener.onFail()
                        }
                    }
                }
                val intent = Intent(context, FloatActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            mPermissionListenerList?.add(permissionListener)
        }

        private var mPermissionListener: PermissionListener? = null
    }
}