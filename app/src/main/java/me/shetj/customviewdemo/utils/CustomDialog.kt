package me.shetj.customviewdemo.utils

import android.content.Context
import android.widget.TextView
import me.shetj.base.tools.json.GsonKit
import me.shetj.base.tools.json.HighStringFormatUtil
import me.shetj.custom.CircleProgressView
import me.shetj.custom.HaloView
import me.shetj.custom.LineWaveView
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.getNextLevelData
import me.shetj.customviewdemo.model.LevelInfo


fun Context.showHaloDialog() {
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

fun Context.showCircleProgressDialog() {
    val test = "{\"consecutiveDays\":3," +
            "\"level\":2}"
    val levelInfo = GsonKit.jsonToBean(test, LevelInfo::class.java)
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


fun Context.showWaveVoice(){
    createDialog(this, R.layout.layout_wave_voice) {
        it.findViewById<LineWaveView>(R.id.line_wave_view).startRecord()
    }
}