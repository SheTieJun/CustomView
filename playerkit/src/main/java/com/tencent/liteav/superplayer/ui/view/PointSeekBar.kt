package com.tencent.liteav.superplayer.ui.view

import android.content.*
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.*
import android.widget.RelativeLayout
import com.tencent.liteav.superplayer.*
import com.tencent.liteav.superplayer.ui.view.PointSeekBar.*

/**
 * 一个带有打点的，模仿seekbar的view
 *
 * 除seekbar基本功能外，还具备关键帧信息打点的功能
 *
 * 1、添加打点信息[.addPoint]
 *
 * 2、自定义thumb[TCThumbView]
 *
 * 3、打点view[TCPointView]
 *
 * 4、打点信息参数[PointParams]
 */
class PointSeekBar : RelativeLayout {
    private var mWidth // 自身宽度
            = 0
    private var mHeight // 自身高度
            = 0
    private var mSeekBarLeft // SeekBar的起点位置
            = 0
    private var mSeekBarRight // SeekBar的终点位置
            = 0
    private var mBgTop // 进度条距离父布局上边界的距离
            = 0
    private var mBgBottom // 进度条距离父布局下边界的距离
            = 0
    private var mRoundSize // 进度条圆角大小
            = 0
    private var mViewEnd // 自身的右边界
            = 0
    private var mNormalPaint // seekbar背景画笔
            : Paint? = null
    private var mProgressPaint // seekbar进度条画笔
            : Paint? = null
    private var mPointerPaint // 打点view画笔
            : Paint? = null
    private var mThumbDrawable // 拖动块图片
            : Drawable? = null
    private var mHalfDrawableWidth // Thumb图片宽度的一半
            = 0

    // Thumb距父布局中的位置
    private var mThumbLeft // thumb的marginLeft值
            = 0f
    private var mThumbRight // thumb的marginRight值
            = 0f
    private var mThumbTop // thumb的marginTop值
            = 0f
    private var mThumbBottom // thumb的marginBottom值
            = 0f
    private var mIsOnDrag // 是否处于拖动状态
            = false
    private var mCurrentLeftOffset = 0f // thumb距离打点view的偏移量
    private var mLastX // 上一次点击事件的横坐标，用于计算偏移量
            = 0f
    private var mCurrentProgress // 当前seekbar的数值
            = 0
    /**
     * 获取seekbar最大值
     *
     * @return
     */
    /**
     * 设置seekbar最大值
     *
     * @param max
     */
    var max = 100 // seekbar最大数值
    private var mBarHeightPx = 0f // seekbar的高度大小 px
    private var mThumbView // 滑动ThumbView
            : TCThumbView? = null
    private var mPointList // 打点信息的列表
            : List<PointParams>? = null
    private var mPointClickListener // 打点view点击回调
            : OnSeekBarPointClickListener? = null
    private var mIsChangePointViews // 打点信息是否更新过
            = false

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }
    /**
     * 获取seekbar进度值
     *
     * @return
     */
    /**
     * 设置seekbar进度值
     *
     * @param progress
     */
    var progress: Int
        get() = mCurrentProgress
        set(progress) {
            var progress = progress
            if (progress < 0) {
                progress = 0
            }
            if (progress > max) {
                progress = max
            }
            if (!mIsOnDrag) {
                mCurrentProgress = progress
                invalidate()
                callbackProgressInternal(progress, false)
            }
        }

    private fun init(attrs: AttributeSet?) {
        setWillNotDraw(false)
        var progressColor = resources.getColor(R.color.superplayer_default_progress_color)
        var backgroundColor =
            resources.getColor(R.color.superplayer_default_progress_background_color)
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SuperPlayerTCPointSeekBar)
            mThumbDrawable =
                a.getDrawable(R.styleable.SuperPlayerTCPointSeekBar_psb_thumbBackground)
            mHalfDrawableWidth = mThumbDrawable!!.intrinsicWidth / 2
            progressColor =
                a.getColor(R.styleable.SuperPlayerTCPointSeekBar_psb_progressColor, progressColor)
            backgroundColor = a.getColor(
                R.styleable.SuperPlayerTCPointSeekBar_psb_backgroundColor,
                backgroundColor
            )
            mCurrentProgress = a.getInt(R.styleable.SuperPlayerTCPointSeekBar_psb_progress, 0)
            max = a.getInt(R.styleable.SuperPlayerTCPointSeekBar_psb_max, 100)
            mBarHeightPx =
                a.getDimension(R.styleable.SuperPlayerTCPointSeekBar_psb_progressHeight, 8f)
            a.recycle()
        }
        mNormalPaint = Paint()
        mNormalPaint!!.color = backgroundColor
        mPointerPaint = Paint()
        mPointerPaint!!.color = Color.RED
        mProgressPaint = Paint()
        mProgressPaint!!.color = progressColor
        post { addThumbView() }
    }

    private fun changeThumbPos() {
        val params = mThumbView!!.layoutParams as LayoutParams
        params.leftMargin = mThumbLeft.toInt()
        params.topMargin = mThumbTop.toInt()
        mThumbView!!.layoutParams = params
    }

    private fun addThumbView() {
        mThumbView = TCThumbView(context, mThumbDrawable)
        val thumbParams =
            LayoutParams(mThumbDrawable!!.intrinsicHeight, mThumbDrawable!!.intrinsicHeight)
        mThumbView!!.layoutParams = thumbParams
        addView(mThumbView)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        mSeekBarLeft = mHalfDrawableWidth
        mSeekBarRight = mWidth - mHalfDrawableWidth
        val barPaddingTop = (mHeight - mBarHeightPx) / 2
        mBgTop = barPaddingTop.toInt()
        mBgBottom = ((mHeight - barPaddingTop).toInt())
        mRoundSize = mHeight / 2
        mViewEnd = mWidth
    }

    private fun calProgressDis() {
        val dis = (mSeekBarRight - mSeekBarLeft) * (mCurrentProgress * 1.0f / max)
        mThumbLeft = dis
        mLastX = mThumbLeft
        mCurrentLeftOffset = 0f
        calculatePointerRect()
    }

    private fun addThumbAndPointViews() {
        post {
            if (mIsChangePointViews) {
                removeAllViews()
                if (mPointList != null) {
                    for (i in mPointList!!.indices) {
                        val params = mPointList!![i]
                        addPoint(params, i)
                    }
                }
                addThumbView()
                mIsChangePointViews = false
            }
            if (!mIsOnDrag) {
                calProgressDis()
                changeThumbPos()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //draw  bg
        val rectF = RectF()
        rectF.left = mSeekBarLeft.toFloat()
        rectF.right = mSeekBarRight.toFloat()
        rectF.top = mBgTop.toFloat()
        rectF.bottom = mBgBottom.toFloat()
        canvas.drawRoundRect(rectF, mRoundSize.toFloat(), mRoundSize.toFloat(), mNormalPaint!!)

        //draw progress
        val pRecf = RectF()
        pRecf.left = mSeekBarLeft.toFloat()
        pRecf.top = mBgTop.toFloat()
        pRecf.right = mThumbRight - mHalfDrawableWidth
        pRecf.bottom = mBgBottom.toFloat()
        canvas.drawRoundRect(
            pRecf,
            mRoundSize.toFloat(), mRoundSize.toFloat(), mProgressPaint!!
        )
        addThumbAndPointViews()
    }

    /**
     * 添加打点view
     *
     * @param pointParams
     * @param index
     */
    fun addPoint(pointParams: PointParams, index: Int) {
        val percent = pointParams.progress * 1.0f / max
        val pointSize = mBgBottom - mBgTop
        val leftMargin = percent * (mSeekBarRight - mSeekBarLeft)
        val rectLeft = ((mThumbDrawable!!.intrinsicWidth - pointSize) / 2).toFloat()
        val rectTop = mBgTop.toFloat()
        val rectBottom = mBgBottom.toFloat()
        val rectRight = rectLeft + pointSize
        val view = TCPointView(context)
        val params = LayoutParams(mThumbDrawable!!.intrinsicWidth, mThumbDrawable!!.intrinsicWidth)
        params.leftMargin = leftMargin.toInt()
        view.setDrawRect(rectLeft, rectTop, rectBottom, rectRight)
        view.layoutParams = params
        view.setColor(pointParams.color)
        view.setOnClickListener {
            if (mPointClickListener != null) {
                mPointClickListener!!.onSeekBarPointClick(view, index)
            }
        }
        addView(view)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false
        var isHandle = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> isHandle = handleDownEvent(event)
            MotionEvent.ACTION_MOVE -> isHandle = handleMoveEvent(event)
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> isHandle = handleUpEvent(event)
        }
        return isHandle
    }

    private fun handleUpEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        if (mIsOnDrag) {
            mIsOnDrag = false
            if (mListener != null) {
                mListener!!.onStopTrackingTouch(this)
            }
            return true
        }
        return false
    }

    private fun handleMoveEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        if (mIsOnDrag) {
            mCurrentLeftOffset = x - mLastX
            //计算出标尺的Rect
            calculatePointerRect()
            if (mThumbRight - mHalfDrawableWidth <= mSeekBarLeft) {
                mThumbLeft = 0f
                mThumbRight = mThumbLeft + mThumbDrawable!!.intrinsicWidth
            }
            if (mThumbLeft + mHalfDrawableWidth >= mSeekBarRight) {
                mThumbRight = mWidth.toFloat()
                mThumbLeft = (mWidth - mThumbDrawable!!.intrinsicWidth).toFloat()
            }
            changeThumbPos()
            invalidate()
            callbackProgress()
            mLastX = x
            return true
        }
        return false
    }

    private fun callbackProgress() {
        if (mThumbLeft == 0f) {
            callbackProgressInternal(0, true)
        } else if (mThumbRight == mWidth.toFloat()) {
            callbackProgressInternal(max, true)
        } else {
            val pointerMiddle = mThumbLeft + mHalfDrawableWidth
            if (pointerMiddle >= mViewEnd) {
                callbackProgressInternal(max, true)
            } else {
                val percent = pointerMiddle / mViewEnd * 1.0f
                var progress = (percent * max).toInt()
                if (progress > max) {
                    progress = max
                }
                callbackProgressInternal(progress, true)
            }
        }
    }

    private fun callbackProgressInternal(progress: Int, isFromUser: Boolean) {
        mCurrentProgress = progress
        if (mListener != null) {
            mListener!!.onProgressChanged(this, progress, isFromUser)
        }
    }

    private fun handleDownEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
//        if (x >= mThumbLeft - 100 && x <= mThumbRight + 100) {
//            if (mListener != null) mListener!!.onStartTrackingTouch(this)
//            mIsOnDrag = true
//            mLastX = x
//            return true
//        }
        mLastX = mThumbLeft
        mIsOnDrag = true
        handleMoveEvent(event)
        return true
    }

    private fun calculatePointerRect() {
        //draw pointer
        val pointerLeft = getPointerLeft(mCurrentLeftOffset)
        val pointerRight = pointerLeft + mThumbDrawable!!.intrinsicWidth
        mThumbLeft = pointerLeft
        mThumbRight = pointerRight
        mThumbTop = 0f
        mThumbBottom = mHeight.toFloat()
    }

    private fun getPointerLeft(offset: Float): Float {
        return mThumbLeft + offset
    }

    private var mListener: OnSeekBarChangeListener? = null
    fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener?) {
        mListener = listener
    }

    interface OnSeekBarChangeListener {
        fun onProgressChanged(seekBar: PointSeekBar, progress: Int, fromUser: Boolean)
        fun onStartTrackingTouch(seekBar: PointSeekBar?)
        fun onStopTrackingTouch(seekBar: PointSeekBar)
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    fun setOnPointClickListener(listener: OnSeekBarPointClickListener?) {
        mPointClickListener = listener
    }

    /**
     * 打点view点击回调
     */
    interface OnSeekBarPointClickListener {
        fun onSeekBarPointClick(view: View, pos: Int)
    }

    /**
     * 设置打点信息列表
     *
     * @param pointList
     */
    fun setPointList(pointList: List<PointParams>?) {
        mPointList = pointList
        mIsChangePointViews = true
        invalidate()
    }

    /**
     * 打点信息
     */
    class PointParams(progress: Int, color: Int) {
        var progress = 0 // 视频进度值(秒)
        var color = Color.RED // 打点view的颜色

        init {
            this.progress = progress
            this.color = color
        }
    }

    /**
     * 打点view
     */
    private class TCPointView : View {
        private var mColor = Color.WHITE // view颜色
        private var mPaint // 画笔
                : Paint? = null
        private var mRectF // 打点view的位置信息(矩形)
                : RectF? = null

        constructor(context: Context?) : super(context) {
            init()
        }

        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
            init()
        }

        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        ) {
            init()
        }

        private fun init() {
            mPaint = Paint()
            mPaint!!.isAntiAlias = true
            mPaint!!.color = mColor
            mRectF = RectF()
        }

        /**
         * 设置打点颜色
         *
         * @param color
         */
        fun setColor(color: Int) {
            mColor = color
            mPaint!!.color = mColor
        }

        /**
         * 设置打点view的位置信息
         *
         * @param left
         * @param top
         * @param right
         * @param bottom
         */
        fun setDrawRect(left: Float, top: Float, right: Float, bottom: Float) {
            mRectF!!.left = left
            mRectF!!.top = top
            mRectF!!.right = right
            mRectF!!.bottom = bottom
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawRect(mRectF!!, mPaint!!)
        }
    }

    /**
     * 拖动块view
     */
    private class TCThumbView(
        context: Context?, // thumb图片
        private val mThumbDrawable: Drawable?
    ) : View(context) {
        private val mPaint // 画笔
                : Paint
        private val mRect // 位置信息(矩形)
                : Rect

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            mRect.left = 0
            mRect.top = 0
            mRect.right = w
            mRect.bottom = h
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            mThumbDrawable!!.bounds = mRect
            mThumbDrawable.draw(canvas)
        }

        init {
            mPaint = Paint()
            mPaint.isAntiAlias = true
            mRect = Rect()
        }
    }
}