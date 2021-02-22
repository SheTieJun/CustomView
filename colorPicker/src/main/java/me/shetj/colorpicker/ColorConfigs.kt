package me.shetj.colorpicker

import me.shetj.base.ktx.loadImageNoCache

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.ArrayMap
import android.widget.ImageView

/**
 * 这个模块的颜色配置部分
 */
object ColorConfig {

    //region other
    private val arrayMap = ArrayMap<String, ColorDrawable>()
    private val colorList: MutableList<String> = ArrayList<String>().apply {
        add("#000000")
        add("#FFFFFF")
        add("#99CCFF")
        add("#FF6666")
        add("#d80bda")
        add("#99cc33")
        add("#a3e496")
        add("#f2fa0d")
        add("#ffcc00")
        add("#fff2e2")
        add("#1fcd9a")
        add("#3cb1ff")
        add("#6bc235")
        add("#fc705d")
        add("#e9ebfe")
        add("#ff9999")
        add("#ff88ea")
        add("#ff6600")
        add("#66cccc")
        add("#faf9de")
        add("#dbdbdb")
    }

    private fun getColorDrawable(item: String): ColorDrawable {
        return arrayMap[item] ?: ColorDrawable(Color.parseColor(item))
            .apply {
                arrayMap[item] = this
            }
    }
    //endregion

    //region public method

    fun showPreview(image: ImageView, item: String) {
        image.apply {
            if (item.startsWith("#")) {
                setImageDrawable(getColorDrawable(item))
            } else {
                loadImageNoCache(item)
            }
        }
    }

    fun getRandomColor() = colorList.shuffled().take(1)[0]
    //endregion

}