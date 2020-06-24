package me.shetj.custom.floatview

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import me.shetj.custom.floatview.FloatActivity.Companion.request

/**
 * Created by yhao on 17-11-14.
 * 7.1及以上需申请权限
 */
internal class FloatPhone(private val mContext: Context) : FloatView() {
    private val mWindowManager: WindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mLayoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
    private var mView: View? = null
    private var mX = 0
    private var mY = 0
    override fun setSize(width: Int, height: Int) {
        mLayoutParams.width = width
        mLayoutParams.height = height
    }

    @Suppress("DEPRECATION")
    override fun setView(view: View?) {
        val layoutType: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        mLayoutParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        mLayoutParams.type = layoutType
        mLayoutParams.windowAnimations = 0
        mView = view
    }

    public override fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        mLayoutParams.gravity = gravity
        mX = xOffset
        mLayoutParams.x = mX
        mY = yOffset
        mLayoutParams.y = mY
    }

    public override fun init() {
        if (Util.hasPermission(mContext)) {
            mLayoutParams.format = PixelFormat.RGBA_8888
            mWindowManager.addView(mView, mLayoutParams)
        } else {
            request(mContext, object : PermissionListener {
                override fun onSuccess() {
                    mLayoutParams.format = PixelFormat.RGBA_8888
                    mWindowManager.addView(mView, mLayoutParams)
                }

                override fun onFail() {}
            })
        }
    }

    public override fun dismiss() {
        mWindowManager.removeView(mView)
    }

    public override fun updateXY(x: Int, y: Int) {
        mX = x
        mLayoutParams.x = mX
        mY = y
        mLayoutParams.y = mY
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    public override fun updateX(x: Int) {
        mX = x
        mLayoutParams.x = mX
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    public override fun updateY(y: Int) {
        mY = y
        mLayoutParams.y = mY
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    public override fun updateWH(width: Int, height: Int) {
        TransitionManager.beginDelayedTransition(mView as ViewGroup?)
        mLayoutParams.width = width
        mLayoutParams.height = height
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    override val x: Int
        get() =  mLayoutParams.x

    override val y: Int
        get() = mLayoutParams.y

}