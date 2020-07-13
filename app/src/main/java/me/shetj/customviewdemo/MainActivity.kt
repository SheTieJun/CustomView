package me.shetj.customviewdemo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_1.*
import kotlinx.android.synthetic.main.item_2.*
import me.shetj.base.ktx.start
import me.shetj.base.tools.json.GsonKit
import me.shetj.base.tools.json.HighStringFormatUtil
import me.shetj.custom.CircleProgressView
import me.shetj.custom.HaloView
import me.shetj.custom.LineWaveView
import me.shetj.customviewdemo.anim.PathAnim
import me.shetj.customviewdemo.floatvideo.destroyFloat
import me.shetj.customviewdemo.floatvideo.showDialogFloat
import me.shetj.customviewdemo.model.LevelInfo
import me.shetj.customviewdemo.pic.PictureInPictureActivity
import me.shetj.customviewdemo.recycle.showDialogRecycle
import me.shetj.customviewdemo.tansition.showDialogLogin
import me.shetj.customviewdemo.utils.MedalDialog
import me.shetj.customviewdemo.utils.MedalDialog.showDialog
import me.shetj.customviewdemo.utils.createDialog
import me.shetj.customviewdemo.utils.showStickyViewDialog


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_cir.setOnClickListener {
            val test = "{\"consecutiveDays\":3," +
                    "\"level\":2}"
            val level = GsonKit.jsonToBean(test, LevelInfo::class.java)
            testCircleProgressView(levelInfo = level)
        }
        btn_halo.setOnClickListener { testHaloView() }
        btn_medal.setOnClickListener { testAnim() }
        btn_fish.setOnClickListener { createDialog(this, R.layout.layout_yinyang_fish) }
        btn_LineWaveVoice.setOnClickListener {
            createDialog(this, R.layout.layout_wave_voice) {
                it.findViewById<LineWaveView>(R.id.line_wave_view).startRecord()
            }
        }
        btn_AngleSeekBar.setOnClickListener { createDialog(this, R.layout.layout_angle_seekbar) }
        btn_Path.setOnClickListener { PathAnim.showPathAnim(this) }
        btn_floatView.setOnClickListener { showDialogFloat(this) }
        btn_recycle.setOnClickListener { showDialogRecycle(this) }
        btn_PictureInPicture.setOnClickListener { start<PictureInPictureActivity>() }
        btn_transition.setOnClickListener { showDialogLogin(this) }
        btn_StickyFinallyView.setOnClickListener { showStickyViewDialog(this) }
    }

    private fun testAnim() {
        showDialog(this, true, R.layout.layout_medal_view)
    }

    private fun testHaloView() {
        createDialog(this, R.layout.layout_halo_view) {
            val haloView: HaloView = it.findViewById(R.id.HaloView)
            haloView.startAnim()
            val change = it.findViewById<TextView>(R.id.change)
            change.setOnClickListener {
                if (haloView.isStart()) {
                    haloView.text = "开始"
                    haloView.stopAnim()
                } else {
                    haloView.text = "录音中"
                    haloView.startAnim()
                }
            }
        }
    }


    private fun testCircleProgressView(levelInfo: LevelInfo? = null) {
        createDialog(this, R.layout.layout_circle_progress_view) { view ->
            levelInfo?.let {
                val nextLevelData = getNextLevelData(levelInfo.level) //获取当前level的下一等级需要多少天
                val needData = nextLevelData - it.consecutiveDays
                view.findViewById<TextView>(R.id.tv_level_info).text =
                    HighStringFormatUtil.buildHighlightString(
                        this, "距下一等级还需坚持学习${needData}天",
                        "$needData", R.color.md_orange_100
                    )
                view.findViewById<TextView>(R.id.tv_level_name)?.apply {
                    text = String.format("LV %s", it.level)
                }
                view.findViewById<CircleProgressView>(R.id.progress_level)?.apply {
                    setProgress((levelInfo.consecutiveDays * 100 / nextLevelData))
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        MedalDialog.hideLoading()
    }

    override fun onStop() {
        super.onStop()
        destroyFloat(true)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
