package com.tencent.liteav.superplayer.model

import android.content.*
import android.os.Bundle
import android.os.Environment.DIRECTORY_MOVIES
import android.text.TextUtils
import android.util.Log
import com.tencent.liteav.basic.log.TXCLog
import com.tencent.liteav.superplayer.SuperPlayerCode
import com.tencent.liteav.superplayer.SuperPlayerDef.*
import com.tencent.liteav.superplayer.SuperPlayerGlobalConfig
import com.tencent.liteav.superplayer.SuperPlayerModel
import com.tencent.liteav.superplayer.SuperPlayerModel.SuperPlayerURL
import com.tencent.liteav.superplayer.SuperPlayerVideoId
import com.tencent.liteav.superplayer.model.entity.PlayImageSpriteInfo
import com.tencent.liteav.superplayer.model.entity.PlayKeyFrameDescInfo
import com.tencent.liteav.superplayer.model.entity.VideoQuality
import com.tencent.liteav.superplayer.model.net.LogReport
import com.tencent.liteav.superplayer.model.protocol.*
import com.tencent.liteav.superplayer.model.utils.VideoQualityUtils
import com.tencent.rtmp.*
import com.tencent.rtmp.TXLiveConstants.NET_STATUS_VIDEO_HEIGHT
import com.tencent.rtmp.TXLiveConstants.NET_STATUS_VIDEO_WIDTH
import com.tencent.rtmp.ui.TXCloudVideoView
import java.util.*
import kotlin.collections.ArrayList

open class SuperPlayerImpl(context: Context?, videoView: TXCloudVideoView?) : SuperPlayer,
        ITXVodPlayListener, ITXLivePlayListener {
    private var isAutoPlay: Boolean = true
    private var mWidth: Int = -1
    private var mHeight: Int = -1
    private var mContext: Context? = null
    private var mVideoView // 腾讯云视频播放view
            : TXCloudVideoView? = null
    private var mCurrentProtocol // 当前视频信息协议类
            : IPlayInfoProtocol? = null
    private var mVodPlayer // 点播播放器
            : TXVodPlayer? = null
    private var mVodPlayConfig // 点播播放器配置
            : TXVodPlayConfig? = null
    private var mLivePlayer // 直播播放器
            : TXLivePlayer? = null
    private var mLivePlayConfig // 直播播放器配置
            : TXLivePlayConfig? = null
    private var mCurrentModel // 当前播放的model
            : SuperPlayerModel? = null
    private var mObserver: SuperPlayerObserver? = null
    private var mVideoQuality: VideoQuality? = null
    override var playerType = PlayerType.VOD // 当前播放类型
    override var playerMode = PlayerMode.WINDOW // 当前播放模式
    override var playerState = PlayerState.END // 当前播放状态
    override var playURL // 当前播放的URL
            : String? = null
    private var mSeekPos // 记录切换硬解时的播放时间
            = 0
    private var mPlaySeekPos // 开始播放直接到对应位置
            = 0
    private var mReportLiveStartTime: Long = -1 // 直播开始时间，用于上报使用时长
    private var mReportVodStartTime: Long = -1 // 点播开始时间，用于上报使用时长
    private var mMaxLiveProgressTime // 观看直播的最大时长
            : Long = 0
    private var mIsMultiBitrateStream // 是否是多码流url播放
            = false
    private var mIsPlayWithFileId // 是否是腾讯云fileId播放
            = false
    private var mDefaultQualitySet // 标记播放多码流url时是否设置过默认画质
            = false
    private var mChangeHWAcceleration // 切换硬解后接收到第一个关键帧前的标记位
            = false

    /**
     * 直播播放器事件回调
     *
     * @param event
     * @param param
     */
    override fun onPlayEvent(event: Int, param: Bundle) {
        if (event != TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            val playEventLog =
                    "TXLivePlayer onPlayEvent event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION)
            TXCLog.d(TAG, playEventLog)
        }
        when (event) {
            TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED, TXLiveConstants.PLAY_EVT_PLAY_BEGIN -> updatePlayerState(
                    PlayerState.PLAYING
            )
            TXLiveConstants.PLAY_ERR_NET_DISCONNECT, TXLiveConstants.PLAY_EVT_PLAY_END -> if (playerType == PlayerType.LIVE_SHIFT) {  // 直播时移失败，返回直播
                mLivePlayer!!.resumeLive()
                updatePlayerType(PlayerType.LIVE)
                onError(SuperPlayerCode.LIVE_SHIFT_FAIL, "时移失败,返回直播")
                updatePlayerState(PlayerState.PLAYING)
            } else {
                stop()
                updatePlayerState(PlayerState.END)
                if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
//                    onError(SuperPlayerCode.NET_ERROR, "网络不给力,点击重试")
                } else {
                    onError(
                            SuperPlayerCode.LIVE_PLAY_END,
                            param.getString(TXLiveConstants.EVT_DESCRIPTION)
                    )
                }
            }
            TXLiveConstants.PLAY_EVT_PLAY_LOADING -> //            case TXLiveConstants.PLAY_WARNING_RECONNECT:  //暂时去掉，回调该状态时，播放画面可能是正常的，loading 状态只在 TXLiveConstants.PLAY_EVT_PLAY_LOADING 处理
                updatePlayerState(PlayerState.LOADING)
            TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME -> {
            }
            TXLiveConstants.PLAY_EVT_STREAM_SWITCH_SUCC -> updateStreamEndStatus(
                    true,
                    PlayerType.LIVE,
                    mVideoQuality
            )
            TXLiveConstants.PLAY_ERR_STREAM_SWITCH_FAIL -> updateStreamEndStatus(
                    false,
                    PlayerType.LIVE,
                    mVideoQuality
            )
            TXLiveConstants.PLAY_EVT_PLAY_PROGRESS -> {
                val progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS)
                mMaxLiveProgressTime =
                        if (progress > mMaxLiveProgressTime) progress.toLong() else mMaxLiveProgressTime
                updatePlayProgress((progress / 1000).toLong(), mMaxLiveProgressTime / 1000)
            }
            else -> {
            }
        }
    }

    /**
     * 直播播放器网络状态回调
     *
     * @param bundle
     */
    override fun onNetStatus(bundle: Bundle) {
        if (playerState == PlayerState.PLAYING) {
            mWidth = bundle.get(NET_STATUS_VIDEO_WIDTH) as Int
            mHeight = bundle.get(NET_STATUS_VIDEO_HEIGHT) as Int
            mObserver?.onVideoSize(mWidth, mHeight)
        }
    }


    /**
     * 点播播放器事件回调
     *
     * @param player
     * @param event
     * @param param
     */
    override fun onPlayEvent(player: TXVodPlayer, event: Int, param: Bundle) {
        if (event != TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            val playEventLog =
                    "TXVodPlayer onPlayEvent event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION)
            TXCLog.d(TAG, playEventLog)
        }
        when (event) {
            TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED -> {
                if (isAutoPlay) {
                    updatePlayerState(PlayerState.LOADING)
                }
                if (mIsMultiBitrateStream) {
                    val bitrateItems: List<TXBitrateItem>? = mVodPlayer!!.supportedBitrates
                    if (bitrateItems == null || bitrateItems.isEmpty()) {
                        return
                    }
                    Collections.sort(bitrateItems) //masterPlaylist多清晰度，按照码率排序，从低到高
                    val videoQualities: ArrayList<VideoQuality> = ArrayList()
                    val size = bitrateItems.size
                    val resolutionNames =
                            if (mCurrentProtocol != null) mCurrentProtocol!!.resolutionNameList else null
                    var i = 0
                    while (i < size) {
                        val bitrateItem = bitrateItems[i]
                        var quality: VideoQuality?
                        quality = if (resolutionNames != null) {
                            VideoQualityUtils.convertToVideoQuality(
                                    bitrateItem,
                                    mCurrentProtocol!!.resolutionNameList
                            )
                        } else {
                            VideoQualityUtils.convertToVideoQuality(bitrateItem, i)
                        }
                        videoQualities.add(quality)
                        i++
                    }
                    if (!mDefaultQualitySet) {
                        mVodPlayer!!.bitrateIndex =
                                bitrateItems[bitrateItems.size - 1].index //默认播放码率最高的
                        mDefaultQualitySet = true
                    }
                    updateVideoQualityList(videoQualities, null)
                }
            }
            TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION -> {
                Log.i(TAG, "mWidth  :$mWidth,mHeight :$mHeight")
                mWidth = player.width
                mHeight = player.height
                mObserver?.onVideoSize(mWidth, mHeight)
            }
            TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME -> if (mChangeHWAcceleration) { //切换软硬解码器后，重新seek位置
                TXCLog.i(TAG, "seek pos:$mSeekPos")
                seek(mSeekPos)
                mChangeHWAcceleration = false
            }
            TXLiveConstants.PLAY_EVT_PLAY_END -> {
                mObserver?.onPlayComplete()
                updatePlayerState(PlayerState.END)
            }
            TXLiveConstants.PLAY_EVT_PLAY_PROGRESS -> {
                val progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS)
                val duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS)
                updatePlayProgress((progress / 1000).toLong(), (duration / 1000).toLong())
            }
            TXLiveConstants.PLAY_EVT_PLAY_BEGIN -> {
                updatePlayerState(PlayerState.PLAYING)
            }
            else -> {

            }
        }
        if (event < 0) { // 播放点播文件失败
            mVodPlayer!!.stopPlay(true)
            updatePlayerState(PlayerState.PAUSE)
            onError(SuperPlayerCode.VOD_PLAY_FAIL, param.getString(TXLiveConstants.EVT_DESCRIPTION))
        }
    }

    /**
     * 点播播放器网络状态回调
     *
     * @param player
     * @param bundle
     */
    override fun onNetStatus(player: TXVodPlayer, bundle: Bundle) {}
    private fun initialize(context: Context?, videoView: TXCloudVideoView?) {
        mContext = context
        mVideoView = videoView
        initLivePlayer(mContext)
        initVodPlayer(mContext)
    }

    /**
     * 初始化点播播放器
     *
     * @param context
     */
    private fun initVodPlayer(context: Context?) {
        mVodPlayer = TXVodPlayer(context)
        val config: SuperPlayerGlobalConfig = SuperPlayerGlobalConfig.instance
        mVodPlayConfig = TXVodPlayConfig().apply {
            val sdcardDir = context!!.getExternalFilesDir(DIRECTORY_MOVIES)
            if (sdcardDir != null) {
                setCacheFolderPath(sdcardDir.path + "/txcache")
            }
            setCacheMp4ExtName("xml")
            setHeaders(config.header)
            setAutoRotate(true)
            setMaxCacheItems(config.maxCacheItem)
        }
        mVodPlayer!!.setRate(config.getSpeedVale())
        mVodPlayer!!.setConfig(mVodPlayConfig)
        mVodPlayer!!.setRenderMode(config.renderMode)
        mVodPlayer!!.setVodListener(this)
        mVodPlayer!!.enableHardwareDecode(config.enableHWAcceleration)
    }

    /**
     * 初始化直播播放器
     *
     * @param context
     */
    private fun initLivePlayer(context: Context?) {
        mLivePlayer = TXLivePlayer(context)
        val config: SuperPlayerGlobalConfig = SuperPlayerGlobalConfig.instance
        mLivePlayConfig = TXLivePlayConfig()
        mLivePlayer!!.setConfig(mLivePlayConfig)
        mLivePlayer!!.setRenderMode(config.liveRenderMode)
        mLivePlayer!!.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT)
        mLivePlayer!!.setPlayListener(this)
        mLivePlayer!!.enableHardwareDecode(config.enableHWAcceleration)
    }

    override fun changeRenderMode(renderMode : Int){
        mLivePlayer?.setRenderMode(renderMode)
        mVodPlayer?.setRenderMode(renderMode)
    }

    /**
     * 播放视频
     *
     * @param model
     */
    fun playWithModel(model: SuperPlayerModel?) {
        mCurrentModel = model
        if (PlayerState.END != playerState ) {
            stop()
        }
        val params = PlayInfoParams()
        params.appId = model!!.appId
        if (model.videoId != null) {
            params.fileId = model.videoId!!.fileId
            params.videoId = model.videoId
            mCurrentProtocol = PlayInfoProtocolV4(params)
        } else if (model.videoIdV2 != null) {
            params.fileId = model.videoIdV2!!.fileId
            params.videoIdV2 = model.videoIdV2
            mCurrentProtocol = PlayInfoProtocolV2(params)
        } else {
            mCurrentProtocol = null // 当前播放的是非v2和v4协议视频，将其置空
        }
        if (model.videoId != null || model.videoIdV2 != null) { // 根据FileId播放
            mCurrentProtocol!!.sendRequest(object : IPlayInfoRequestCallback {
                override fun onSuccess(protocol: IPlayInfoProtocol?, param: PlayInfoParams) {
                    TXCLog.i(TAG, "onSuccess: protocol params = $param")
                    mReportVodStartTime = System.currentTimeMillis()
                    mVodPlayer!!.setPlayerView(mVideoView)
                    playModeVideo(mCurrentProtocol!!)
                    autoPlay(isAutoPlay)
                    updatePlayerType(PlayerType.VOD)
                    updatePlayProgress(0, 0)
                    updateVideoImageSpriteAndKeyFrame(
                            mCurrentProtocol!!.imageSpriteInfo,
                            mCurrentProtocol!!.keyFrameDescInfo
                    )
                }

                override fun onError(errCode: Int, message: String) {
                    TXCLog.i(TAG, "onFail: errorCode = $errCode message = $message")
                    this@SuperPlayerImpl.onError(
                            SuperPlayerCode.VOD_REQUEST_FILE_ID_FAIL,
                            "播放视频文件失败 code = $errCode msg = $message"
                    )
                }
            })
        } else { // 根据URL播放
            var videoURL: String? = null
            val videoQualities: ArrayList<VideoQuality> = ArrayList()
            var defaultVideoQuality: VideoQuality? = null
            if (model.multiURLs != null && !model.multiURLs!!.isEmpty()) { // 多码率URL播放
                var i = 0
                for (superPlayerURL in model.multiURLs!!) {
                    if (i == model.playDefaultIndex) {
                        videoURL = superPlayerURL!!.url
                    }
                    videoQualities.add(
                            VideoQuality(
                                    i++,
                                    superPlayerURL!!.qualityName,
                                    superPlayerURL.url
                            )
                    )
                }
                defaultVideoQuality = videoQualities[model.playDefaultIndex]
            } else if (!TextUtils.isEmpty(model.url)) { // 传统URL模式播放
                videoURL = model.url
            }
            if (TextUtils.isEmpty(videoURL)) {
                onError(SuperPlayerCode.PLAY_URL_EMPTY, "播放视频失败，播放链接为空")
                return
            }
            if (isRTMPPlay(videoURL)) { // 直播播放器：普通RTMP流播放
                mReportLiveStartTime = System.currentTimeMillis()
                mLivePlayer!!.setPlayerView(mVideoView)
                playLiveURL(videoURL, TXLivePlayer.PLAY_TYPE_LIVE_RTMP)
            } else if (isFLVPlay(videoURL)) { // 直播播放器：直播FLV流播放
                mReportLiveStartTime = System.currentTimeMillis()
                mLivePlayer!!.setPlayerView(mVideoView)
                playTimeShiftLiveURL(model.appId, videoURL)
                if (model.multiURLs != null && !model.multiURLs!!.isEmpty()) {
                    startMultiStreamLiveURL(videoURL)
                }
            } else { // 点播播放器：播放点播文件
                mReportVodStartTime = System.currentTimeMillis()
                mVodPlayer!!.setPlayerView(mVideoView)
                playVodURL(videoURL)
            }
            val isLivePlay = isRTMPPlay(videoURL) || isFLVPlay(videoURL)
            updatePlayerType(if (isLivePlay) PlayerType.LIVE else PlayerType.VOD)
            updatePlayProgress(0, 0)
            updateVideoQualityList(videoQualities, defaultVideoQuality)
        }
    }

    /**
     * 播放v2或v4协议视频
     *
     * @param protocol
     */
    private fun playModeVideo(protocol: IPlayInfoProtocol) {
        playVodURL(protocol.url)
        val videoQualities = protocol.videoQualityList
        mIsMultiBitrateStream = videoQualities == null
        val defaultVideoQuality = protocol.defaultVideoQuality
        updateVideoQualityList(videoQualities, defaultVideoQuality)
    }

    /**
     * 播放非v2和v4协议视频
     *
     * @param model
     */
    private fun playModeVideo(model: SuperPlayerModel?) {
        if (model!!.multiURLs != null && !model.multiURLs!!.isEmpty()) { // 多码率URL播放
            for (i in model.multiURLs!!.indices) {
                if (i == model.playDefaultIndex) {
                    playVodURL(model.multiURLs!![i]!!.url)
                }
            }
        } else if (!TextUtils.isEmpty(model.url)) {
            playVodURL(model.url)
        }
    }

    /**
     * 播放直播URL
     */
    private fun playLiveURL(url: String?, playType: Int) {
        playURL = url
        if (mLivePlayer != null) {
            mLivePlayer!!.setPlayListener(this)
            val result = mLivePlayer!!.startPlay(
                    url,
                    playType
            ) // result返回值：0 success;  -1 empty url; -2 invalid url; -3 invalid playType;
            if (result != 0) {
                TXCLog.e(TAG, "playLiveURL videoURL:$url,result:$result")
            } else {
                updatePlayerState(PlayerState.LOADING)
            }
        }
    }

    /**
     * 播放点播url
     */
    private fun playVodURL(url: String?) {
        if (url == null || "" == url) {
            return
        }
        playURL = url
        if (url.contains(".m3u8")) {
            mIsMultiBitrateStream = true
        }
        if (mVodPlayer != null) {
            mDefaultQualitySet = false
            mVodPlayer!!.setStartTime(mPlaySeekPos.toFloat())
            mPlaySeekPos = 0
            mVodPlayer!!.setAutoPlay(isAutoPlay)
            mVodPlayer!!.setVodListener(this)
            if (mCurrentProtocol != null) {
                TXCLog.d(TAG, "TOKEN: " + mCurrentProtocol!!.token)
                mVodPlayer!!.setToken(mCurrentProtocol!!.token)
            } else {
                mVodPlayer!!.setToken(null)
            }
            val ret = mVodPlayer!!.startPlay(url)
            if (!isAutoPlay){
                updatePlayerState(PlayerState.PAUSE)
            }
        }
        mIsPlayWithFileId = false
    }

    /**
     * 播放时移直播url
     */
    private fun playTimeShiftLiveURL(appId: Int, url: String?) {
        val bizid = url!!.substring(url.indexOf("//") + 2, url.indexOf("."))
        val domian: String = SuperPlayerGlobalConfig.instance.playShiftDomain
        val streamid = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."))
        TXCLog.i(TAG, "bizid:$bizid,streamid:$streamid,appid:$appId")
        playLiveURL(url, TXLivePlayer.PLAY_TYPE_LIVE_FLV)
        var bizidNum = -1
        try {
            bizidNum = bizid.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            TXCLog.e(TAG, "playTimeShiftLiveURL: bizidNum error = $bizid")
        }
        mLivePlayer!!.prepareLiveSeek(domian, bizidNum)
    }

    /**
     * 配置多码流url
     *
     * @param url
     */
    private fun startMultiStreamLiveURL(url: String?) {
        mLivePlayConfig!!.setAutoAdjustCacheTime(false)
        mLivePlayConfig!!.setMaxAutoAdjustCacheTime(5f)
        mLivePlayConfig!!.setMinAutoAdjustCacheTime(5f)
        mLivePlayer!!.setConfig(mLivePlayConfig)
        if (mObserver != null) {
            mObserver!!.onPlayTimeShiftLive(mLivePlayer, url)
        }
    }

    /**
     * 上报播放时长
     */
    private fun reportPlayTime() {
        if (mReportLiveStartTime != -1L) {
            val reportEndTime = System.currentTimeMillis()
            val diff = (reportEndTime - mReportLiveStartTime) / 1000
            LogReport.instance
                    .uploadLogs(LogReport.ELK_ACTION_LIVE_TIME, diff, 0)
            mReportLiveStartTime = -1
        }
        if (mReportVodStartTime != -1L) {
            val reportEndTime = System.currentTimeMillis()
            val diff = (reportEndTime - mReportVodStartTime) / 1000
            LogReport.instance.uploadLogs(
                    LogReport.ELK_ACTION_VOD_TIME,
                    diff,
                    if (mIsPlayWithFileId) 1 else 0
            )
            mReportVodStartTime = -1
        }
    }

    /**
     * 更新播放进度
     *
     * @param current  当前播放进度(秒)
     * @param duration 总时长(秒)
     */
    private fun updatePlayProgress(current: Long, duration: Long) {
        if (mObserver != null) {
            mObserver!!.onPlayProgress(current, duration)
        }
    }

    /**
     * 更新播放类型
     *
     * @param playType
     */
    private fun updatePlayerType(playType: PlayerType) {
        if (playType != playerType) {
            playerType = playType
        }
        if (mObserver != null) {
            mObserver!!.onPlayerTypeChange(playType)
        }
    }

    /**
     * 更新播放状态
     *
     * @param playState
     */
    private fun updatePlayerState(playState: PlayerState) {
        playerState = playState
        if (mObserver == null) {
            return
        }
        when (playState) {
            PlayerState.PLAYING -> mObserver!!.onPlayBegin()
            PlayerState.PAUSE -> mObserver!!.onPlayPause()
            PlayerState.LOADING -> mObserver!!.onPlayLoading()
            PlayerState.END -> mObserver!!.onPlayStop()
        }
    }

    private fun updateStreamStartStatus(
            success: Boolean,
            playerType: PlayerType,
            quality: VideoQuality
    ) {
        if (mObserver != null) {
            mObserver!!.onSwitchStreamStart(success, playerType, quality)
        }
    }

    private fun updateStreamEndStatus(
            success: Boolean,
            playerType: PlayerType,
            quality: VideoQuality?
    ) {
        if (mObserver != null) {
            mObserver!!.onSwitchStreamEnd(success, playerType, quality)
        }
    }

    private fun updateVideoQualityList(
            videoQualities: ArrayList<VideoQuality>?,
            defaultVideoQuality: VideoQuality?
    ) {
        if (mObserver != null) {
            mObserver!!.onVideoQualityListChange(videoQualities, defaultVideoQuality)
        }
    }

    private fun updateVideoImageSpriteAndKeyFrame(
            info: PlayImageSpriteInfo?,
            list: ArrayList<PlayKeyFrameDescInfo>?
    ) {
        if (mObserver != null) {
            mObserver!!.onVideoImageSpriteAndKeyFrameChanged(info, list)
        }
    }

    private fun onError(code: Int, message: String?) {
        if (mObserver != null) {
            mObserver!!.onError(code, message)
        }
    }

    private val playName: String?
        private get() {
            var title: String? = ""
            if (mCurrentModel != null && !TextUtils.isEmpty(mCurrentModel!!.title)) {
                title = mCurrentModel!!.title
            } else if (mCurrentProtocol != null && !TextUtils.isEmpty(mCurrentProtocol!!.name)) {
                title = mCurrentProtocol!!.name
            }
            return title
        }

    /**
     * 是否是RTMP协议
     *
     * @param videoURL
     * @return
     */
    private fun isRTMPPlay(videoURL: String?): Boolean {
        return !TextUtils.isEmpty(videoURL) && videoURL!!.startsWith("rtmp")
    }

    /**
     * 是否是HTTP-FLV协议
     *
     * @param videoURL
     * @return
     */
    private fun isFLVPlay(videoURL: String?): Boolean {
        return (!TextUtils.isEmpty(videoURL) && videoURL!!.startsWith("http://")
                || videoURL!!.startsWith("https://")) && videoURL.contains(".flv")
    }

    override fun play(url: String?) {
        val model = SuperPlayerModel()
        model.url = url!!
        playWithModel(model)
    }

    override fun play(appId: Int, url: String?) {
        val model = SuperPlayerModel()
        model.appId = appId
        model.url = url!!
        playWithModel(model)
    }

    override fun play(appId: Int, fileId: String?, psign: String?) {
        val videoId = SuperPlayerVideoId()
        videoId.fileId = fileId
        videoId.pSign = psign
        val model = SuperPlayerModel()
        model.appId = appId
        model.videoId = videoId
        playWithModel(model)
    }

    override fun play(appId: Int, superPlayerURLS: List<SuperPlayerURL?>?, defaultIndex: Int) {
        val model = SuperPlayerModel()
        model.appId = appId
        model.multiURLs = superPlayerURLS
        model.playDefaultIndex = defaultIndex
        playWithModel(model)
    }

    override fun reStart() {
        if (playerType == PlayerType.LIVE || playerType == PlayerType.LIVE_SHIFT) {
            if (isRTMPPlay(playURL)) {
                playLiveURL(playURL, TXLivePlayer.PLAY_TYPE_LIVE_RTMP)
            } else if (isFLVPlay(playURL)) {
                playTimeShiftLiveURL(mCurrentModel!!.appId, playURL)
                if (mCurrentModel!!.multiURLs != null && !mCurrentModel!!.multiURLs!!.isEmpty()) {
                    startMultiStreamLiveURL(playURL)
                }
            }
        } else {
            playVodURL(playURL)
            mVodPlayer!!.setAutoPlay(true)
        }

    }

    override fun pause() {
        if (playerType == PlayerType.VOD) {
            mVodPlayer!!.pause()
        } else {
            mLivePlayer!!.pause()
        }
        updatePlayerState(PlayerState.PAUSE)
    }

    override fun pauseVod() {
        if (playerType == PlayerType.VOD) {
            mVodPlayer!!.pause()
        }
        updatePlayerState(PlayerState.PAUSE)
    }

    override fun resume() {
        if (playerType == PlayerType.VOD) {
            mVodPlayer!!.resume()
        } else {
            mLivePlayer!!.resume()
        }
        updatePlayerState(PlayerState.PLAYING)
    }

    override fun resumeLive() {
        if (playerType == PlayerType.LIVE_SHIFT) {
            mLivePlayer!!.resumeLive()
        }
        updatePlayerType(PlayerType.LIVE)
    }

    override fun autoPlay(auto: Boolean) {
        this.isAutoPlay = auto
        if (playerType == PlayerType.VOD) {
            mVodPlayer?.setAutoPlay(auto)
        }
    }

    override fun getWidth(): Int {
        return mWidth
    }

    override fun getHeight(): Int {
        return mHeight
    }


    override fun stop() {
        if (mVodPlayer != null) {
            mVodPlayer!!.stopPlay(false)
        }
        if (mLivePlayer != null) {
            mLivePlayer!!.stopPlay(false)
            mVideoView!!.removeVideoView()
        }
        updatePlayerState(PlayerState.END)
        reportPlayTime()
    }

    override fun destroy() {
        mCurrentModel = null
    }
    override fun switchPlayMode(playerMode: PlayerMode) {
        if (this.playerMode == playerMode) {
            return
        }
        this.playerMode = playerMode
    }

    override fun enableHardwareDecode(enable: Boolean) {
        if (playerType == PlayerType.VOD) {
            mChangeHWAcceleration = true
            mVodPlayer!!.enableHardwareDecode(enable)
            mSeekPos = mVodPlayer!!.currentPlaybackTime.toInt()
            TXCLog.i(TAG, "save pos:$mSeekPos")
            stop()
            if (mCurrentProtocol == null) {    // 当protocol为空时，则说明当前播放视频为非v2和v4视频
                playModeVideo(mCurrentModel)
            } else {
                playModeVideo(mCurrentProtocol!!)
            }
            autoPlay(true)
        } else {
            mLivePlayer!!.enableHardwareDecode(enable)
            playWithModel(mCurrentModel)
        }
        // 硬件加速上报
        if (enable) {
            LogReport.instance
                    .uploadLogs(LogReport.ELK_ACTION_HW_DECODE, 0, 0)
        } else {
            LogReport.instance
                    .uploadLogs(LogReport.ELK_ACTION_SOFT_DECODE, 0, 0)
        }
    }

    override fun setPlayerView(videoView: TXCloudVideoView?) {
        if (playerType == PlayerType.VOD) {
            mVodPlayer!!.setPlayerView(videoView)
        } else {
            mLivePlayer!!.setPlayerView(videoView)
        }
    }

    /**
     * 只有播放前设置有效
     */
    override fun setPlayToSeek(position: Int) {
        if (playerState == PlayerState.END) {
            mPlaySeekPos = position
        }else{
            seek(position)
        }
    }

    override fun seek(position: Int) {
        if (playerType == PlayerType.VOD) {
            if (mVodPlayer != null) {
                mVodPlayer!!.seek(position)
            }
        }
//        else {
//            updatePlayerType(PlayerType.LIVE_SHIFT)
//            LogReport.instance
//                .uploadLogs(LogReport.ELK_ACTION_TIMESHIFT, 0, 0)
//            if (mLivePlayer != null) {
//                mLivePlayer!!.seek(position)
//            }
//        }
        if (mObserver != null) {
            mObserver!!.onSeek(position)
        }
    }

    override fun snapshot(listener: TXLivePlayer.ITXSnapshotListener) {
        if (playerType == PlayerType.VOD) {
            mVodPlayer!!.snapshot(listener)
        } else if (playerType == PlayerType.LIVE) {
            mLivePlayer!!.snapshot(listener)
        } else {
            listener.onSnapshot(null)
        }
    }

    override fun setRate(speedLevel: Float) {
        if (playerType == PlayerType.VOD) {
            mVodPlayer!!.setRate(speedLevel)
        }
        //速度改变上报
        LogReport.instance
                .uploadLogs(LogReport.ELK_ACTION_CHANGE_SPEED, 0, 0)
    }

    override fun setMirror(isMirror: Boolean) {
        if (playerType == PlayerType.VOD) {
            mVodPlayer!!.setMirror(isMirror)
        }
        if (isMirror) {
            LogReport.instance
                    .uploadLogs(LogReport.ELK_ACTION_MIRROR, 0, 0)
        }
    }

    override fun switchStream(quality: VideoQuality) {
        mVideoQuality = quality
        if (playerType == PlayerType.VOD) {
            if (mVodPlayer != null) {
                if (quality.url != null) { // br!=0;index=-1;url!=null   //br=0;index!=-1;url!=null
                    // 说明是非多bitrate的m3u8子流，需要手动seek
                    val isPlay = playerState == PlayerState.PLAYING
                    val currentTime = mVodPlayer!!.currentPlaybackTime
                    mVodPlayer!!.stopPlay(true)
                    TXCLog.i(TAG, "onQualitySelect quality.url:" + quality.url)
                    mVodPlayer!!.setStartTime(currentTime)
                    mVodPlayer!!.startPlay(quality.url)
                    mVodPlayer!!.setAutoPlay(isPlay)
                } else { //br!=0;index!=-1;url=null
                    TXCLog.i(TAG, "setBitrateIndex quality.index:" + quality.index)
                    // 说明是多bitrate的m3u8子流，会自动无缝seek
                    mVodPlayer!!.bitrateIndex = quality.index
                }
                updateStreamStartStatus(true, PlayerType.VOD, quality)
            }
        } else {
            var success = false
            if (mLivePlayer != null && !TextUtils.isEmpty(quality.url)) {
                val result = mLivePlayer!!.switchStream(quality.url)
                success = result >= 0
            }
            updateStreamStartStatus(success, PlayerType.LIVE, quality)
        }
        //清晰度上报
        LogReport.instance
                .uploadLogs(LogReport.ELK_ACTION_CHANGE_RESOLUTION, 0, 0)
    }

    override fun setObserver(observer: SuperPlayerObserver?) {
        mObserver = observer
    }

    companion object {
        private const val TAG = "SuperPlayerImpl"
    }

    init {
        initialize(context, videoView)
    }
}