package me.shetj.customviewdemo.tx.video

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tencent.liteav.superplayer.SimPlayerCallBack
import com.tencent.liteav.superplayer.SuperPlayerDef
import com.tencent.liteav.superplayer.SuperPlayerModel
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.app.ArmsUtils.Companion.fullScreencall
import me.shetj.base.tools.app.ArmsUtils.Companion.statuInScreen
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.databinding.ActivityScorllPlayerBinding

class ScorllPlayerActivity : BaseBindingActivity<BaseViewModel, ActivityScorllPlayerBinding>() {

    var isScroll = true

    override fun initViewBinding(): ActivityScorllPlayerBinding {
        return ActivityScorllPlayerBinding.inflate(layoutInflater)
    }

    override fun onActivityCreate() {
        super.onActivityCreate()
        ArmsUtils.statuInScreen2(this, isBlack = true)
        mViewBinding.playView.setPlayerViewCallback(object : SimPlayerCallBack() {
            override fun onStartFullScreenPlay() {
                super.onStartFullScreenPlay()
                fullScreencall(this@ScorllPlayerActivity)
                mViewBinding.videoContent.removeView(mViewBinding.playView)
                mViewBinding.root.addView(mViewBinding.playView)
            }

            override fun onStopFullScreenPlay() {
                super.onStopFullScreenPlay()
                statuInScreen(true)
                mViewBinding.root.removeView(mViewBinding.playView)
                mViewBinding.videoContent.addView(mViewBinding.playView)
            }

            override fun onClickShare() {
                super.onClickShare()
            }
        })
        val model = SuperPlayerModel().apply {
            val url =
                    "https://vod.lycheer.net/e22cd48bvodtransgzp1253442168/be6011f25285890787514054429/v.f20.mp4"
            multiURLs = ArrayList<SuperPlayerModel.SuperPlayerURL>().apply {
                add(SuperPlayerModel.SuperPlayerURL(url, "流畅"))
                add(SuperPlayerModel.SuperPlayerURL(url, "标清"))
                add(SuperPlayerModel.SuperPlayerURL(url, "高清"))
            }
        }
        mViewBinding.playView.playWithModel(model)
        mViewBinding.playView.setAutoPlay(true)
        initViewPage2()
        mViewBinding.btnChange.setOnClickListener {
            if (isScroll) {
                isScroll = false
                mViewBinding.barLayout.enableAppBar(isScroll)
            } else {
                isScroll = true
                mViewBinding.barLayout.enableAppBar(isScroll)
            }

        }
    }


    fun initViewPage2() {
        val fragments = arrayListOf<Fragment>(EmptyFragment(), EmptyFragment())
        mViewBinding.tabLayout.apply {
            tabPadding = 15f
            indicatorWidth = 30f
            indicatorHeight = 3f
            setIndicatorMargin(0f, 1f, 0f, 2f)
            textBold = 1
            indicatorColor = ContextCompat.getColor(context, R.color.orange_main_color)
        }.setViewPager(mViewBinding.viewPager, arrayListOf("简介", "作业"), this, fragments)
    }

    override fun onResume() {
        super.onResume()
        // 重新开始播放
        if (mViewBinding.playView.playerState == SuperPlayerDef.PlayerState.PLAYING) {
            mViewBinding.playView.onResume()
            if (mViewBinding.playView.playerMode == SuperPlayerDef.PlayerMode.FLOAT) {
                mViewBinding.playView.switchPlayMode(SuperPlayerDef.PlayerMode.WINDOW)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        // 停止播放
        if (mViewBinding.playView.playerMode != SuperPlayerDef.PlayerMode.FLOAT) {
            mViewBinding.playView.onPause()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mViewBinding.playView.playerMode != SuperPlayerDef.PlayerMode.FLOAT) {
            mViewBinding.playView.resetPlayer()
            mViewBinding.playView.release()
        }
    }
}