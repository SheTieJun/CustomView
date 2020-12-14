package me.shetj.customviewdemo

import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_1.*
import kotlinx.android.synthetic.main.item_2.*
import kotlinx.android.synthetic.main.item_3.*
import kotlinx.android.synthetic.main.item_4.*
import kotlinx.android.synthetic.main.item_5.*
import me.shetj.base.ktx.*
import me.shetj.base.tools.app.FragmentUtils
import me.shetj.base.view.TipPopupWindow
import me.shetj.customviewdemo.anim.PathAnim
import me.shetj.customviewdemo.country.AreaCodeModel
import me.shetj.customviewdemo.country.CountryBottomDialog
import me.shetj.customviewdemo.floatvideo.destroyFloat
import me.shetj.customviewdemo.floatvideo.showDialogFloat
import me.shetj.customviewdemo.lihuaindex.LHIndexActivity
import me.shetj.customviewdemo.pic.PictureInPictureActivity
import me.shetj.customviewdemo.pre_video.PreVideoActivity
import me.shetj.customviewdemo.recorder.RecorderPopup
import me.shetj.customviewdemo.recycle.PinnedRecycleActivity
import me.shetj.customviewdemo.recycle.showDialogRecycle
import me.shetj.customviewdemo.sticker.StickerActivity
import me.shetj.customviewdemo.tansition.showDialogLogin
import me.shetj.customviewdemo.text.createShowTextDialog
import me.shetj.customviewdemo.tx.video.TXPlayerActivity
import me.shetj.customviewdemo.utils.*
import me.shetj.customviewdemo.utils.MedalDialog.showDialog
import me.shetj.customviewdemo.water_mark.onImageActivityResult
import me.shetj.customviewdemo.water_mark.selectImage
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {


    private var jsonList: MutableList<AreaCodeModel>? = null
    private val phoneDialog: CountryBottomDialog by lazy {
        CountryBottomDialog(
            this,
            jsonList ?: ArrayList()
        )
    }
    private val recorderPopup: RecorderPopup by lazy {
        RecorderPopup(this) {
            it.logi()
            it.showToast()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_cir.setOnClickListener { showCircleProgressDialog() }
        btn_halo.setOnClickListener { showHaloDialog() }
        btn_medal.setOnClickListener { showDialog(this, true, R.layout.layout_medal_view) }
        btn_fish.setOnClickListener { createDialog(this, R.layout.layout_yinyang_fish) }
        btn_LineWaveVoice.setOnClickListener { showWaveVoice() }
        btn_AngleSeekBar.setOnClickListener { createDialog(this, R.layout.layout_angle_seekbar) }
        btn_Path.setOnClickListener { PathAnim.showPathAnim(this) }
        btn_floatView.setOnClickListener { showDialogFloat(this) }
        btn_recycle.setOnClickListener { showDialogRecycle(this) }
        btn_PictureInPicture.setOnClickListener { start<PictureInPictureActivity>() }
        btn_transition.setOnClickListener { showDialogLogin(this) }
        btn_StickyFinallyView.setOnClickListener { showStickyViewDialog(this) }
        btn_water_mark.setOnClickListener { selectImage(this) }
        btn_sticker.setOnClickListener { start<StickerActivity>() }
        btn_popup.setOnClickListener { showQMUIPopup() }
        btn_fragment.setOnClickListener {
            PathAnim.showFragmentAnim(
                this,
                fragmentManager = supportFragmentManager
            )
        }
        btn_change_text.setOnClickListener { showChangeText() }
        btn_text_font.setOnClickListener { createShowTextDialog(this) }
        btn_loading.setOnClickListener { createLoading(this) }
        btn_videoPre.setOnClickListener { start<PreVideoActivity>() }
        country.setOnClickListener { showCountry() }
        index.setOnClickListener { start<LHIndexActivity>() }
        btn_tx_player.setOnClickListener { start<TXPlayerActivity>() }
        btn_recycle_spine.setOnClickListener { start<PinnedRecycleActivity>() }
        btn_record.setOnClickListener { recorderPopup.showPop() }
    }

    private fun showCountry() {
        this.launch {
            doOnIO {
                if (jsonList.isNullOrEmpty()) {
                    val json = getJson("country.json")
                    jsonList = json.toList<AreaCodeModel>()?.toMutableList()
                }
                doOnMain {
                    phoneDialog.showBottomSheet()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onImageActivityResult(this, requestCode, resultCode, data)
    }

    override fun onPause() {
        super.onPause()
        MedalDialog.hideLoading()
    }

    override fun onStop() {
        super.onStop()
        destroyFloat(true)
    }


    override fun onBackPressed() {
        if (!FragmentUtils.dispatchBackPress(supportFragmentManager)) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getJson(fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            val assetManager: AssetManager = assets
            val bf = BufferedReader(
                InputStreamReader(
                    assetManager.open(fileName)
                )
            )
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

}
