package me.shetj.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


/**
 * 可以变的text
 */
class ChangeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            paint.shader = LinearGradient(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                Color.DKGRAY,
                Color.RED,
                Shader.TileMode.CLAMP
            )
            invalidate() //need 否则第一次不会展示
        }
    }
}