package me.shetj.customviewdemo.pre_video

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import java.lang.Thread.sleep


class VideoCode {


    private val TAG = this::class.java.simpleName
    private val TIME_OUT: Long = 10 * 1000

    private var mediaExtractor: MediaExtractor? = null
    private var videoPath: Uri? = null
    private var mCodec: MediaCodec? = null//解码器
    private var mSurfaceView: Surface? = null
    private var isEOS: Boolean = false
    private var context: Context? = null

    fun setVideoPath(context: Context, videoPath: Uri, surfaceView: SurfaceView? = null) {
        this.context = context
        this.videoPath = videoPath
        this.mSurfaceView = surfaceView?.holder?.surface
    }


    fun startExtractor() {
        initExtractor()
        Thread { decoderVideo() }.start()
    }

    private fun initExtractor() {
        if (videoPath == null) {
            Log.e(TAG, "videoPath no null")
            return
        }
        try {
            mediaExtractor = MediaExtractor()
            //设置视频地址
            mediaExtractor?.setDataSource(context!!, videoPath!!, null)
            //获取轨道数量
            val trackCount = mediaExtractor!!.trackCount

            for (i in 0 until trackCount) {

                val mediaFormat = mediaExtractor!!.getTrackFormat(i)
                val mimeType = mediaFormat.getString(MediaFormat.KEY_MIME)
                if (mimeType?.startsWith("video/", false) == true) {
                    mediaExtractor?.selectTrack(i)
                    initVideo(mediaFormat)
                    break
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun initVideo(mediaFormat: MediaFormat) {
        val mimeType = mediaFormat.getString(MediaFormat.KEY_MIME)
        if (mCodec == null) {
            mCodec = MediaCodec.createDecoderByType(mimeType!!)
        }
        mCodec?.configure(mediaFormat, mSurfaceView, null, 0)
        mCodec?.start()
    }


    private fun decoderVideo() {
        val start = System.currentTimeMillis()
        isEOS = false
        val bufferInfo = MediaCodec.BufferInfo()
        try {
            while (!isEOS && mCodec != null && mediaExtractor != null) {

                val inIndex = mCodec!!.dequeueInputBuffer(-1) //设置time out
                if (inIndex >= 0) {
                    val inputBuffer = mCodec!!.getInputBuffer(inIndex)
                    inputBuffer?.clear()
                    val sampleSize = mediaExtractor!!.readSampleData(inputBuffer!!, 0)
                    if (sampleSize > 0) {
                        mCodec!!.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        isEOS = true
                    } else {
                        mCodec!!.queueInputBuffer(inIndex, 0, sampleSize, mediaExtractor!!.sampleTime, 0)
                        mediaExtractor!!.advance()
                    }
                }

                val outIndex = mCodec!!.dequeueOutputBuffer(bufferInfo, TIME_OUT)

                when (outIndex) {
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    }
                    MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    }
                    else -> {
                        mCodec!!.getOutputBuffer(outIndex)
                        while (bufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis() - start) {
                            try {
                                sleep(10)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                break
                            }
                        }
                        mCodec!!.releaseOutputBuffer(outIndex, true)
                    }
                }

                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    isEOS = true
                    releaseDecode()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            isEOS = true
            releaseDecode()
        }
    }


    private fun releaseDecode() {
        try {
            if (mCodec != null) {
                mCodec!!.stop()
                mCodec!!.release()
            }
            if (mediaExtractor != null) {
                mediaExtractor!!.release()
            }
        } catch (e: Exception) {
            Log.e("mixRecorder", "message = ${e.message}")
        }
    }

    fun release() {
        isEOS = true
        releaseDecode()
    }
}