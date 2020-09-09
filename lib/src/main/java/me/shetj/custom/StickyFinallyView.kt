package me.shetj.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import me.shetj.custom.base.BaseCustomView
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin


/**
 * qq汽包
 * 1.
 */
class StickyFinallyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseCustomView(context, attrs, defStyleAttr) {


    private var mContext: Context = context
    private val mPaint = Paint().apply {
        color = Color.RED;
        isAntiAlias = true;
        style = Paint.Style.FILL;
    }
    private val mPath = Path();

    private var mCenter: PointF? = null  //原始圆
    private var mFixCircle: PointF? = null
    private var mFlexibleCircle: PointF? = null
    private var mFixTangent: Array<PointF?>? = null
    private var mFlexibleTangent: Array<PointF?>? = null
    private val mControl = PointF();
    private val mRadius = 20f
    private var mIsDraw = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenter = PointF((w / 2).toFloat(), (h / 2).toFloat())
        mFixCircle = PointF((w / 2).toFloat(), (h / 2).toFloat())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mIsDraw = true
                    mFlexibleCircle = PointF(event.x, event.y)
                    postInvalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    mFlexibleCircle!![event.x] = event.y
                    postInvalidate()
                }
                MotionEvent.ACTION_UP -> {
                    mIsDraw = false
                    postInvalidate()
                }
            }
        return true
    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mFixCircle?.let {
            canvas?.drawCircle(mFixCircle!!.x, mFixCircle!!.y, mRadius, mPaint);
        }
        if (mIsDraw) {
            canvas?.drawCircle(mFlexibleCircle!!.x, mFlexibleCircle!!.y, mRadius, mPaint);
            mControl.set(
                (mFixCircle!!.x + mFlexibleCircle!!.x) / 2,
                (mFixCircle!!.y + mFlexibleCircle!!.y) / 2
            );
            val dy = mFlexibleCircle!!. y -mFixCircle!!.y;
            val dx = mFlexibleCircle!!. x -mFixCircle!!.x;
            if (dx != 0f) {
                val k1 = dy / dx
                val k2 = - 1 / k1
                mFlexibleTangent = getTangentPoints(mFlexibleCircle!!, mRadius,  k2);
                mFixTangent = getTangentPoints(mFixCircle!!, mRadius,   k2);
            } else {
                mFlexibleTangent = getTangentPoints(mFlexibleCircle!!, mRadius,  0f);
                mFixTangent = getTangentPoints(mFixCircle!!, mRadius, 0f);
            }
            mPath.reset();
            mPath.moveTo(mFixTangent!![0]!!.x, mFixTangent!![0]!!.y);
            mPath.quadTo(mControl.x, mControl.y, mFlexibleTangent!![0]!!.x, mFlexibleTangent!![0]!!.y);
            mPath.lineTo(mFlexibleTangent!![1]!!.x, mFlexibleTangent!![1]!!.y);
            mPath.quadTo(mControl.x, mControl.y, mFixTangent!![1]!!.x, mFixTangent!![1]!!.y);
            mPath.close();
            canvas?.drawPath(mPath, mPaint);
        }

    }

    private fun getTangentPoints(
        pointF: PointF,
        radius: Float,
        lineK: Float?
    ): Array<PointF?> {
        val pointFS = arrayOfNulls<PointF>(2)
        val radian: Float
        val xOffset: Float
        val yOffset: Float
        if (lineK != null) {
            radian = atan(lineK)
            xOffset = (cos(radian.toDouble()) * radius).toFloat()
            yOffset = (sin(radian.toDouble()) * radius).toFloat()
        } else {
            xOffset = radius
            yOffset = 0f
        }
        pointFS[0] = PointF(pointF.x + xOffset, pointF.y + yOffset)
        pointFS[1] = PointF(pointF.x - xOffset, pointF.y - yOffset)
        return pointFS
    }

}