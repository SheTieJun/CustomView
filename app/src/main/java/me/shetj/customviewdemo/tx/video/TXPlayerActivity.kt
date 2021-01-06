package me.shetj.customviewdemo.tx.video

import android.view.View
import com.tencent.liteav.superplayer.GlobalConfig
import com.tencent.liteav.superplayer.SimPlayerCallBack
import com.tencent.liteav.superplayer.SuperPlayerDef
import com.tencent.liteav.superplayer.SuperPlayerModel
import com.tencent.liteav.superplayer.tv.TVControl
import kotlinx.android.synthetic.main.activity_pre_videoe.view.*
import me.shetj.base.ktx.showToast
import me.shetj.base.ktx.start
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.customviewdemo.databinding.ActivityTXPlayerBinding


class TXPlayerActivity : BaseBindingActivity<BaseViewModel, ActivityTXPlayerBinding>() {

    private var isTv: Boolean = false
    private var iskey: Boolean = false

    override fun initViewBinding(): ActivityTXPlayerBinding {
        return ActivityTXPlayerBinding.inflate(layoutInflater)
    }


    override fun onActivityCreate() {
        super.onActivityCreate()
        ArmsUtils.statuInScreen2(this)
        initVideoInfo()
    }

    private fun initVideoInfo() {
        val model = SuperPlayerModel()
//        model.url =
//            "https://vod2.lycheer.net/73e20c6evodtranscq1253442168/d238a8f65285890807609259635/v.f117442.mp4?t=5fdb67b0&us=ab85cd03d9&sign=2e34d0d67a2f4272d1edf3ad421bbf93"
//        model.url =
//            "http://200024424.vod.myqcloud.com/200024424_709ae516bdf811e6ad39991f76a4df69.f20.mp4"
        mViewBinding.superVodPlayerView.setPlayToSeek(10)
        mViewBinding.superVodPlayerView.playWithModel(model)
        mViewBinding.superVodPlayerView.setAutoPlay(true)
        mViewBinding.superVodPlayerView.setPlayerViewCallback(object : SimPlayerCallBack() {
            override fun onClickShare() {
                super.onClickShare()
                "点击了分享".showToast()
            }

            override fun onStartFullScreenPlay() {
                super.onStartFullScreenPlay()
                "全屏播放".showToast()
                ArmsUtils.fullScreencall(this@TXPlayerActivity)
            }

            override fun onStopFullScreenPlay() {
                super.onStopFullScreenPlay()
                "停止全屏播放".showToast()
                ArmsUtils.statuInScreen2(this@TXPlayerActivity,true)
            }

            override fun onStartFloatWindowPlay() {
                super.onStartFloatWindowPlay()
                "悬浮窗播放".showToast()
            }

            override fun onClickFloatCloseBtn() {
                super.onClickFloatCloseBtn()
                "退出悬浮播放".showToast()
            }
        })
        mViewBinding.btnFloatView.setOnClickListener {
            mViewBinding.superVodPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.FLOAT)
            finish()
        }
        mViewBinding.btnKey.setOnClickListener {
            if (!iskey) {
                mViewBinding.superVodPlayerView.setKeyList(
                    "测试列表",
                    KeyListAdapter(ArrayList<String>().apply {
                        repeat(10) {
                            add("播放item$it")
                        }
                    }).apply {
                        setOnItemClickListener { adapter, view, position ->
                            getItem(position).showToast()
                        }
                    },
                    onNext = {
                        it.toString().showToast()
                    })
                iskey = true
            }else{
                mViewBinding.superVodPlayerView.setKeyList(null,null)
                iskey = false
            }
            mViewBinding.btnKey.text = "设置播放列表KeyList:$iskey"
        }

        mViewBinding.btnUrl.setOnClickListener {
            val model = SuperPlayerModel().apply {
                val url =
                    "http://200024424.vod.myqcloud.com/200024424_709ae516bdf811e6ad39991f76a4df69.f20.mp4"
                multiURLs = ArrayList<SuperPlayerModel.SuperPlayerURL>().apply {
                    add(SuperPlayerModel.SuperPlayerURL(url, "流程"))
                    add(SuperPlayerModel.SuperPlayerURL(url, "标清"))
                    add(SuperPlayerModel.SuperPlayerURL(url, "高清"))
                }
            }
            mViewBinding.superVodPlayerView.playWithModel(model)
            mViewBinding.superVodPlayerView.setAutoPlay(true)
            mViewBinding.btnUrl.text = "已设置多url"
        }

        mViewBinding.btnTv.setOnClickListener {
            isTv = if (!isTv) {
                mViewBinding.superVodPlayerView.setTVControl(object : TVControl {
                    override fun startShowTVLink() {
                        "点击了投屏".showToast()
                    }
                })
                true
            }else{
                mViewBinding.superVodPlayerView.setTVControl(null)
                false
            }
            mViewBinding.btnTv.text = "设置TV:$isTv"
        }
        mViewBinding.btnHide.setOnClickListener {
            GlobalConfig.instance.isHideAll = !GlobalConfig.instance.isHideAll
            if (GlobalConfig.instance.isHideAll){
                mViewBinding.superVodPlayerView.hideAll()
            }
            mViewBinding.btnHide.text = "隐藏控制：${GlobalConfig.instance.isHideAll}"
        }
        mViewBinding.btnScoll.setOnClickListener { start<ScorllPlayerActivity>() }
        mViewBinding.btnScoll2.setOnClickListener { start<ScorllPlayer2Activity>() }
    }

    override fun onResume() {
        super.onResume()
        // 重新开始播放
        if (mViewBinding.superVodPlayerView.playerState == SuperPlayerDef.PlayerState.PLAYING) {
            mViewBinding.superVodPlayerView.onResume()
            if (mViewBinding.superVodPlayerView.playerMode == SuperPlayerDef.PlayerMode.FLOAT) {
                mViewBinding.superVodPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.WINDOW)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        // 停止播放
        if (mViewBinding.superVodPlayerView.playerMode != SuperPlayerDef.PlayerMode.FLOAT) {
            mViewBinding.superVodPlayerView.onPause()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mViewBinding.superVodPlayerView.playerMode != SuperPlayerDef.PlayerMode.FLOAT) {
            mViewBinding.superVodPlayerView.resetPlayer()
            mViewBinding.superVodPlayerView.release()
        }
    }
}