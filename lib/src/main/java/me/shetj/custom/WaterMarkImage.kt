package me.shetj.custom

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import java.io.File


/**
 * 添加水印
 * 好友有一种更好的实现方式 ：
 * https://blog.csdn.net/wxm1225929690/article/details/80019300
 */
class WaterMarkImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var mImage: Bitmap? = null
    private var mText: String = "水印"
    private var z: Int = 0
    private var mTextSpacing: Float = 20f
    private var rect = Rect()
    private var mTextSize = 50f
    private var mTextColor = Color.BLACK
    private var mTextAlpha = 1f
    private var mTextRotation = 0f
    private var isShowWaterMark = true

    protected val TAG = this.javaClass.simpleName

    private val mPaint = Paint()
    private val mTextPaint = TextPaint().apply {
        color = Color.BLACK
        textSize = mTextSize
    }


    init {

        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.WaterMarkImage).apply {
                mTextColor = getColor(R.styleable.WaterMarkImage_android_textColor, Color.DKGRAY)
                mText = getString(R.styleable.WaterMarkImage_android_text) ?: "水印"
                mTextRotation = getFloat(R.styleable.WaterMarkImage_textRotation, 0f)
                mTextSize =  getDimension(R.styleable.WaterMarkImage_android_textSize,mTextSize)
                isShowWaterMark = getBoolean(R.styleable.WaterMarkImage_isShowWaterMark,true)
                mTextSpacing = getFloat(R.styleable.WaterMarkImage_textLineSpacing,20f)
                mTextAlpha = getFloat(R.styleable.WaterMarkImage_textAlpha,1f)
                initConfig()
            }.recycle()
        }
    }

    private fun initConfig() {
        mTextPaint.apply {
            textSize = mTextSize
            color = mTextColor
            textSize = mTextSize
            alpha = (mTextAlpha * 255).toInt()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        z = z(width, height)
    }

    /**
     * 设置图片
     */
    fun setImage(url: String) {
        if (!File(url).exists()) {
            Log.e(TAG, "文件不存在：$url")
            return
        }
        if (mImage != null && !mImage!!.isRecycled) {
            setImageBitmap(null)
            mImage!!.recycle()
        }

        mImage = BitmapFactory.decodeFile(url, BitmapFactory.Options())

        if (mImage == null) {
            Log.e(TAG, "文件错误，无法生成bitmap")
            return
        }
        deal()
    }

    /**
     * 设置图片
     */
    fun setImage(url: Uri) {

        if (mImage != null && !mImage!!.isRecycled) {
            setImageBitmap(null)
            mImage!!.recycle()
        }

        mImage =  getBitmapFromUri(context,url)

        if (mImage == null) {
            Log.e(TAG, "文件错误，无法生成bitmap")
            return
        }
        deal()
    }

    private fun deal() {
        setImageBitmap(dealWith(mImage))
    }

    /**
     *
     */
    private fun dealWith(mImage: Bitmap?): Bitmap? {
        if (!isShowWaterMark) {
            return mImage
        }
        mImage?.let {
            val config = it.config
            val sourceBitmapHeight = it.height
            val sourceBitmapWidth = it.width

            val shareBitmap = Bitmap.createBitmap(sourceBitmapWidth, sourceBitmapHeight, config)
            val canvas = Canvas(shareBitmap)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(it, 0f, 0f, mPaint)
            drawWaterMark(canvas, it)

            return shareBitmap
        }
        return null
    }

    fun setTextMark(text: String) {
        this.mText = text
        deal()
    }


    fun enableWaterMark() {
        isShowWaterMark = true
        deal()
    }

    fun disEnableWaterMark() {
        isShowWaterMark = false
        deal()
    }

    fun setTextAlpha(alpha: Float){
        mTextAlpha = alpha
        initConfig()
        deal()
    }

    fun setTextSize(textSize: Float){
        mTextSize = textSize
        initConfig()
        deal()
    }

    fun setTextRotation(rotation:Float){
        mTextRotation = rotation
        deal()
    }

    fun save(filename: String): Boolean {
        Log.i(TAG, "文件 = $filename")

        mImage?.let {
            val config = it.config
            val sourceBitmapHeight = it.height
            val sourceBitmapWidth = it.width

            val shareBitmap = Bitmap.createBitmap(sourceBitmapWidth, sourceBitmapHeight, config)
            val canvas = Canvas(shareBitmap)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(it, 0f, 0f, mPaint)
            drawWaterMark(canvas, it)

            return shareBitmap.saveImageBitmap(filename)
        }
        return false
    }


//    这是另外一种实现方式
//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//        if (isShowWaterMark) {
//            drawWaterMark(canvas)
//        }
//    }
//
//    private fun drawWaterMark(canvas: Canvas?) {
//        mTextPaint.getTextBounds(mText, 0, mText.length, rect)
//        canvas?.rotate(mTextRotation, width / 2f, height / 2f)
//        val textWidth = rect.width()
//        val textHeight = rect.height()
//        val def =  -(z - width) / 2.toFloat()
//        var lastX = def
//        var lastY = -(z - height) / 2.toFloat()
//        val sizeX = (z / (textWidth + mTextSpacing) + 1).toInt()
//        val sizeY = (z / (textHeight + mTextSpacing) + 1).toInt()
//        for (i in 0..sizeY) {
//            for (j in 0..sizeX) {
//                canvas?.drawText(
//                    mText,
//                    lastX,
//                    lastY,
//                    mTextPaint
//                )
//                lastX += textWidth + mTextSpacing
//            }
//            lastY += mTextSpacing + textHeight
//            lastX = def
//        }
//    }



    private fun drawWaterMark(canvas: Canvas?, bitmap: Bitmap) {
        val z = z(bitmap.width, bitmap.height)
        canvas?.rotate(mTextRotation, bitmap.width / 2f, bitmap.height / 2f)
        mTextPaint.getTextBounds(mText, 0, mText.length, rect)
        val textWidth = rect.width()
        val textHeight = rect.height()

        val def = -(z - bitmap.width) / 2.toFloat()

        var lastX = def
        var lastY = -(z - bitmap.height) / 2.toFloat()
        val sizeX = (z / (textWidth + mTextSpacing) + 1).toInt()
        val sizeY = (z / (textHeight + mTextSpacing) + 1).toInt()
        for (i in 0..sizeY) {
            for (j in 0..sizeX) {
                canvas?.drawText(
                    mText,
                    lastX,
                    lastY,
                    mTextPaint
                )
                lastX += textWidth + mTextSpacing
            }
            lastY += mTextSpacing + textHeight
            lastX = def
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mImage?.recycle()
        mImage == null
    }

}