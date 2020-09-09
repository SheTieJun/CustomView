package me.shetj.customviewdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_1.*
import kotlinx.android.synthetic.main.item_2.*
import kotlinx.android.synthetic.main.item_3.*
import me.shetj.base.ktx.start
import me.shetj.customviewdemo.anim.PathAnim
import me.shetj.customviewdemo.floatvideo.destroyFloat
import me.shetj.customviewdemo.floatvideo.showDialogFloat
import me.shetj.customviewdemo.pic.PictureInPictureActivity
import me.shetj.customviewdemo.recycle.showDialogRecycle
import me.shetj.customviewdemo.sticker.StickerActivity
import me.shetj.customviewdemo.tansition.showDialogLogin
import me.shetj.customviewdemo.utils.*
import me.shetj.customviewdemo.utils.MedalDialog.showDialog
import me.shetj.customviewdemo.water_mark.onImageActivityResult
import me.shetj.customviewdemo.water_mark.selectImage


class MainActivity : AppCompatActivity() {

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
        btn_popup.setOnClickListener { btn_popup.showQMUIPopup() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onImageActivityResult(this,requestCode, resultCode, data)
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
