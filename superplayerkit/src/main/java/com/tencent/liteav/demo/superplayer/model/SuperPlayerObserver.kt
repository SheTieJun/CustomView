package com.tencent.liteav.demo.superplayer.model

import com.tencent.liteav.demo.superplayer.SuperPlayerDef.PlayerType
import com.tencent.liteav.demo.superplayer.model.entity.PlayImageSpriteInfo
import com.tencent.liteav.demo.superplayer.model.entity.PlayKeyFrameDescInfo
import com.tencent.liteav.demo.superplayer.model.entity.VideoQuality
import com.tencent.rtmp.TXLivePlayer

abstract class SuperPlayerObserver {
    /**
     * 开始播放
     * @param name 当前视频名称
     */
    open fun onPlayBegin(name: String?) {}

    /**
     * 播放暂停
     */
    open fun onPlayPause() {}

    /**
     * 播放器停止
     */
    open fun onPlayStop() {}

    /**
     * 播放器进入Loading状态
     */
    open fun onPlayLoading() {}


    open fun onVideoSize(width:Int,height:Int){

    }
    /**
     * 播放进度回调
     *
     * @param current
     * @param duration
     */
    open fun onPlayProgress(current: Long, duration: Long) {}
    open fun onSeek(position: Int) {}
    open fun onSwitchStreamStart(success: Boolean, playerType: PlayerType, quality: VideoQuality) {}
    open fun onSwitchStreamEnd(success: Boolean, playerType: PlayerType, quality: VideoQuality?) {}
    open fun onError(code: Int, message: String?) {}
    open fun onPlayerTypeChange(playType: PlayerType?) {}
    open fun onPlayTimeShiftLive(player: TXLivePlayer?, url: String?) {}
    open fun onVideoQualityListChange(
        videoQualities: List<VideoQuality?>?,
        defaultVideoQuality: VideoQuality?
    ) {
    }

    open fun onVideoImageSpriteAndKeyFrameChanged(
        info: PlayImageSpriteInfo?,
        list: List<PlayKeyFrameDescInfo?>?
    ) {
    }
}