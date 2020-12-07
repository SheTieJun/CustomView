package me.shetj.customviewdemo.surfview

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView


class PlayerTextureView  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : TextureView(context, attrs, defStyle), TextureView.SurfaceTextureListener {
    private var mRequestedAspect = -1.0
    private var mSurface: Surface? = null

    val surface: Surface?
        get() = mSurface

    fun onPause() {}
    fun onResume() {}

    /**
     * set aspect ratio of this view
     * `aspect ratio = width / height`.
     */
    fun setAspectRatio(aspectRatio: Double) {
        require(aspectRatio >= 0)
        if (mRequestedAspect != aspectRatio) {
            mRequestedAspect = aspectRatio
            requestLayout()
        }
    }

    /**
     * measure view size with keeping aspect ratio
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        if (mRequestedAspect > 0) {
            var initialWidth: Int = MeasureSpec.getSize(widthMeasureSpec)
            var initialHeight: Int = MeasureSpec.getSize(heightMeasureSpec)
            val horizPadding: Int = paddingLeft + paddingRight
            val vertPadding: Int = paddingTop + paddingBottom
            initialWidth -= horizPadding
            initialHeight -= vertPadding
            val viewAspectRatio = initialWidth.toDouble() / initialHeight
            val aspectDiff = mRequestedAspect / viewAspectRatio - 1

            // stay size if the difference of calculated aspect ratio is small enough from specific value
            if (Math.abs(aspectDiff) > 0.01) {
                if (aspectDiff > 0) {
                    // adjust heght from width
                    initialHeight = (initialWidth / mRequestedAspect).toInt()
                } else {
                    // adjust width from height
                    initialWidth = (initialHeight * mRequestedAspect).toInt()
                }
                initialWidth += horizPadding
                initialHeight += vertPadding
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(initialWidth, MeasureSpec.EXACTLY)
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(initialHeight, MeasureSpec.EXACTLY)
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    init {
        surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        if (mSurface != null) mSurface!!.release()
        mSurface = Surface(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        if (mSurface != null) {
            mSurface!!.release()
            mSurface = null
        }
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }
}