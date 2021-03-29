package me.shetj.customviewdemo

import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import me.shetj.base.ktx.*
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.base.tools.app.FragmentUtils
import me.shetj.customviewdemo.anim.DynamicAnim
import me.shetj.customviewdemo.anim.PathAnim
import me.shetj.customviewdemo.anim.SpringAnim
import me.shetj.customviewdemo.behavior.BeHaviorActivity
import me.shetj.customviewdemo.country.AreaCodeModel
import me.shetj.customviewdemo.country.CountryBottomDialog
import me.shetj.customviewdemo.databinding.ActivityMainBinding
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
import me.shetj.customviewdemo.utils.CalendarKit.addCalendar
import me.shetj.customviewdemo.utils.MedalDialog.showDialog
import me.shetj.customviewdemo.water_mark.onImageActivityResult
import me.shetj.customviewdemo.water_mark.selectImage
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class MainActivity : BaseBindingActivity<BaseViewModel, ActivityMainBinding>() {


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

    override fun onActivityCreate() {
        super.onActivityCreate()
        mViewBinding.apply {
            btnCir.setOnClickListener { showCircleProgressDialog() }
            btnWaterMark.setOnClickListener { selectImage(this@MainActivity) }
            btnRecycle.setOnClickListener { showDialogRecycle(this@MainActivity) }
            btnHalo.setOnClickListener { showHaloDialog() }
            btnTransition.setOnClickListener { showDialogLogin(this@MainActivity) }
            btnStickyFinallyView.setOnClickListener { showStickyViewDialog(this@MainActivity) }
            btnMedal.setOnClickListener { showDialog(this@MainActivity, true, R.layout.layout_medal_view) }
            btnSticker.setOnClickListener { start<StickerActivity>() }
            btnPopup.setOnClickListener { showQMUIPopup() }
            btnFish.setOnClickListener { createDialog(this@MainActivity, R.layout.layout_yinyang_fish) }
            btnChangeText.setOnClickListener { showChangeText() }
            btnTextFont.setOnClickListener { createShowTextDialog(this@MainActivity) }
            btnFragment.setOnClickListener {
                PathAnim.showFragmentAnim(
                        this@MainActivity,
                        fragmentManager = supportFragmentManager
                )
            }
            btnLineWaveVoice.setOnClickListener { showWaveVoice() }
            btnLoading.setOnClickListener { createLoading(this@MainActivity) }
            btnVideoPre.setOnClickListener { start<PreVideoActivity>() }
            btnAngleSeekBar.setOnClickListener { createDialog(this@MainActivity, R.layout.layout_angle_seekbar) }
            btnPath.setOnClickListener { PathAnim.showPathAnim(this@MainActivity) }
            btnFloatView.setOnClickListener { showDialogFloat(this@MainActivity) }
            btnPictureInPicture.setOnClickListener { start<PictureInPictureActivity>() }
            country.setOnClickListener { showCountry() }
            index.setOnClickListener { start<LHIndexActivity>() }
            btnTxPlayer.setOnClickListener { start<TXPlayerActivity>() }
            btnRecycleSpine.setOnClickListener { start<PinnedRecycleActivity>() }
            btnRecord.setOnClickListener { recorderPopup.showPop() }
            btnBehavior.setOnClickListener { start<BeHaviorActivity>() }
            btnDyAnim.setOnClickListener { DynamicAnim.showDyAnim(this@MainActivity) }
            btnSpringAnim.setOnClickListener { SpringAnim.showSpringAnim(this@MainActivity) }
            btnAddCalendar.setOnClickListener { addCalendar() }
        }
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
