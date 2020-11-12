package me.shetj.customviewdemo.pre_video

import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.result.ActivityResultCallback
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_pre_videoe.*
import me.shetj.base.ktx.logi
import me.shetj.base.ktx.toJson
import me.shetj.base.tools.file.FileQUtils.searchFile
import me.shetj.customviewdemo.R
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class PreVideoActivity : AppCompatActivity() {

    val publishSubject = PublishSubject.create<Int>()
    val videoCode:VideoCode by lazy { VideoCode() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_videoe)

        publishSubject.throttleFirst(500,TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                "seekTo:$it".logi()
            }
        player.setOnPreparedListener {
            seekBar.max = player.duration
        }
        btn_select.setOnClickListener {
            searchFile(type = "video/*",
                ActivityResultCallback<Uri?> { result ->
                    result.toJson().logi()
                    player.setVideoURI(result)
                    videoCode.setVideoPath(this,result,surface)
                    videoCode.startExtractor()
                })
        }
        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progress.toString().logi()
                if (abs(player.currentPosition - progress) > 500) {
                    player.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                player.pause()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        videoCode.release()
    }

}