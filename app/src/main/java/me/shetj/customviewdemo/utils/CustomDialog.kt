package me.shetj.customviewdemo.utils

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.widget.popup.QMUIPopups
import com.qmuiteam.qmui.widget.popup.QMUIQuickAction
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


fun View.showQMUIPopup(){

    QMUIPopups.quickAction(
        context,
        QMUIDisplayHelper.dp2px(context, 56),
        QMUIDisplayHelper.dp2px(context, 56)
    )
        .shadow(true)
        .bgColorAttr(R.attr.qmui_popup_new_color)
        .skinManager(QMUISkinManager.defaultInstance(context))
        .edgeProtection(QMUIDisplayHelper.dp2px(context, 20))
        .addAction(QMUIQuickAction.Action().icon(R.drawable.ic_baseline_file_copy_24).text("复制")
            .onClick { quickAction, action, position ->
                quickAction.dismiss()
                Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
            })
        .addAction(QMUIQuickAction.Action().icon(R.drawable.ic_baseline_format_underlined_24).text("划线")
            .onClick { quickAction, action, position ->
                quickAction.dismiss()
                Toast.makeText(context, "划线成功", Toast.LENGTH_SHORT).show()
            })
        .addAction(QMUIQuickAction.Action().icon(R.drawable.ic_baseline_share_24).text("分享")
            .onClick { quickAction, action, position ->
                quickAction.dismiss()
                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show()
            })
        .addAction(QMUIQuickAction.Action().icon(R.drawable.ic_baseline_text_format_24)
            .text("删除划线").onClick { quickAction, action, position ->
                quickAction.dismiss()
                Toast.makeText(context, "删除划线成功", Toast.LENGTH_SHORT).show()
            })
        .addAction(QMUIQuickAction.Action().icon(R.drawable.ic_baseline_brush_24).text("词典")
            .onClick { quickAction, action, position ->
                quickAction.dismiss()
                Toast.makeText(context, "打开词典", Toast.LENGTH_SHORT).show()
            })
        .addAction(QMUIQuickAction.Action().icon(R.drawable.ic_baseline_blur_circular_24).text("圈圈")
            .onClick { quickAction, action, position ->
                quickAction.dismiss()
                Toast.makeText(context, "查询成功", Toast.LENGTH_SHORT).show()
            })
        .addAction(QMUIQuickAction.Action().icon(R.drawable.ic_baseline_find_in_page_24).text("查询")
            .onClick { quickAction, action, position ->
                quickAction.dismiss()
                Toast.makeText(context, "查询成功", Toast.LENGTH_SHORT).show()
            })
        .show(this)

}