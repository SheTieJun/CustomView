package me.shetj.customviewdemo.recorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import me.shetj.custom.base.BaseCustomView
import me.shetj.customviewdemo.R
import kotlin.math.min


class RecodingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseCustomView(context, attrs, defStyleAttr) {

    private val paint:Paint = Paint().apply {
        color = ContextCompat.getColor(context,R.color.purple_700)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            val mHeight = height - paddingLeft - paddingRight
            val mWith = width - paddingTop - paddingBottom
            val radius = min(mHeight, mWith)
            drawCircle((mWith/2).toFloat(), (mHeight/2).toFloat(), (radius/2).toFloat(),paint)
        }
    }


}