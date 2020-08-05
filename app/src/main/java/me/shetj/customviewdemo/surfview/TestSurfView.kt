package me.shetj.customviewdemo.surfview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView


class TestSurfView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
    }

    private var mCanvas: Canvas?=null
    private var mSurfaceHolder: SurfaceHolder??=null
    private var mPaint: Paint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {


    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        draw()
    }


    private fun draw() {

        try {
            System.out.println("============draw========");
             mCanvas = holder.lockCanvas();
            mCanvas?.drawCircle(500f,500f,300f,mPaint);
            mCanvas?.drawCircle(100f,100f,20f,mPaint);
        } catch (e:Exception ) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null)
                holder.unlockCanvasAndPost(mCanvas);
        }
    }

}