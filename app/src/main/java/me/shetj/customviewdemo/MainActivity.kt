package me.shetj.customviewdemo

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import me.shetj.base.kt.loadImage
import me.shetj.base.kt.loadImageBitmap
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.json.GsonKit
import me.shetj.base.tools.json.StringFormatUtil
import me.shetj.custom.CircleProgressView
import me.shetj.customviewdemo.model.LevelInfo

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_cir.setOnClickListener {
            val test = "{\"total_learn_days\":71.0,\"today_learn_time\":0,\"consecutive_days\":3," +
                    "\"week_rank\":0,\"total_learn_hours\":48.64,\"score\":21,\"medal_info\":{\"status\":0," +
                    "\"tip\":\"\"},\"card_surface_bubble\":false,\"card_pop_window\":false," +
                    "\"calendar_red_point\":true,\"level\":2,\"level_prompt\":0}"
            val level = GsonKit.jsonToBean(test, LevelInfo::class.java)
            testCircleProgressView(levelInfo = level)
        }
    }


    private fun testCircleProgressView(url: String = "https://staticqc.lycheer.net/account3/static/media/levelrule.45f3b2f1.png", levelInfo: LevelInfo? = null){
        val view = layoutInflater.inflate(R.layout.layout_circle_progress_view, null)
        val image: ImageView = view.findViewById(R.id.iv_level_des)
        Glide.with(this)
                .load(url  )
                .into(image)
        levelInfo?.let {
            val nextLevelData = getNextLevelData(levelInfo.level) //获取当前level的下一等级需要多少天
            val needData = nextLevelData - it.consecutive_days
            view.findViewById<TextView>(R.id.tv_level_info).text = StringFormatUtil(this, "距下一等级还需坚持学习${needData}天",
                    "$needData",R.color.md_orange_100).fillColor()
            view.findViewById<TextView>(R.id.tv_level_name)?.apply {
                text = String.format("LV %s",it.level)
            }
            view.findViewById<CircleProgressView>(R.id.progress_level)?.apply {
                setProgress((levelInfo.consecutive_days*100/nextLevelData))
            }
        }
        AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("取消") { dialog, which -> dialog.dismiss() }
                .setNegativeButton("确定") { dailog, which-> dailog.dismiss() }
                .show()?.apply {
                    window?.setLayout(ArmsUtils.dip2px(300f), LinearLayout.LayoutParams.WRAP_CONTENT);
                }
    }
}
