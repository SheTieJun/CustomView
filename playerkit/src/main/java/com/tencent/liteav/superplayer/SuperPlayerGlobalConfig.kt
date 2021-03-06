package com.tencent.liteav.superplayer

import com.tencent.rtmp.TXLiveConstants

typealias GlobalConfig = SuperPlayerGlobalConfig

/**
 *
 * 超级播放器全局配置类
 * 必须在view 初始化之前设置
 */
class SuperPlayerGlobalConfig {
    companion object Singleton {
        val instance = SuperPlayerGlobalConfig()
    }

    val REFERER = "https://m.lizhiweike.com"

    val header = HashMap<String, String>().apply {
        put("Referer", REFERER)
    }

    /**
     * 默认播放填充模式 （ 默认播放模式为 自适应模式 ）
     */
    var renderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION

    var liveRenderMode = TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN

    /**
     * 播放器最大缓存个数 （ 默认缓存 5 ）
     */
    var maxCacheItem = 5

    /**
     * 是否启用悬浮窗 （ 默认开启 true ）
     */
    var enableFloatWindow = true

    /**
     * 是否开启硬件加速 （ 默认开启硬件加速 ）
     */
    var enableHWAcceleration = true

    var isShowMirror = false //展示镜像功能

    var isHideAll = false

    var isShowAccelerate = true //展示已经加速功能

    var isShowPlayStyle = true //展示播放模式

    var isShowTimeStyle = true //展示定时

    var speed: Float = 1.0f

    fun getSpeedVale(): Float = speed

    /**
     * 时移域名 （修改为自己app的时移域名）
     */
    var playShiftDomain = "liteavapp.timeshift.qcloud.com"

    /**
     * 悬浮窗位置 （ 默认在左上角，初始化一个宽为 810，高为 540的悬浮窗口 ）
     */
    var floatViewRect = TXRect(0, 0, 810, 540)

    class TXRect {
        var x = 0
        var y = 0
        var width = 0
        var height = 0

        internal constructor(x: Int, y: Int, width: Int, height: Int) {
            this.x = x
            this.y = y
            this.width = width
            this.height = height
        }

    }

}