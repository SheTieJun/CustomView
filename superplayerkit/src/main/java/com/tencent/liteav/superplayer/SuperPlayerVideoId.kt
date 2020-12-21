package com.tencent.liteav.superplayer

/**
 * Created by hans on 2019/3/25.
 * 使用腾讯云fileId播放
 */
class SuperPlayerVideoId {
    var fileId // 腾讯云视频fileId
            : String? = null
    var pSign // v4 开启防盗链必填
            : String? = null

    override fun toString(): String {
        return "SuperPlayerVideoId{" +
                ", fileId='" + fileId + '\'' +
                ", pSign='" + pSign + '\'' +
                '}'
    }
}