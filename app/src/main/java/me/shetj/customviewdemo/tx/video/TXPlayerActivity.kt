package me.shetj.customviewdemo.tx.video

import com.tencent.liteav.demo.superplayer.SuperPlayerDef
import com.tencent.liteav.demo.superplayer.SuperPlayerModel
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.customviewdemo.databinding.ActivityTXPlayerBinding


class TXPlayerActivity : BaseBindingActivity<BaseViewModel, ActivityTXPlayerBinding>() {

    override fun initViewBinding(): ActivityTXPlayerBinding {
        return ActivityTXPlayerBinding.inflate(layoutInflater)
    }


    override fun onActivityCreate() {
        super.onActivityCreate()
        ArmsUtils.fullScreencall(this)
        initVideoInfo()
    }

    private fun initVideoInfo() {
        val model = SuperPlayerModel()
        model.url =
            "https://vod2.lycheer.net/73e20c6evodtranscq1253442168/d238a8f65285890807609259635/v.f117442.mp4?t=5fdb67b0&us=ab85cd03d9&sign=2e34d0d67a2f4272d1edf3ad421bbf93"
//        model.url =
//            "http://200024424.vod.myqcloud.com/200024424_709ae516bdf811e6ad39991f76a4df69.f20.mp4"
        mViewBinding.superVodPlayerView.playWithModel(model)
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
        mViewBinding.superVodPlayerView.resetPlayer()
        mViewBinding.superVodPlayerView.release()
    }
}