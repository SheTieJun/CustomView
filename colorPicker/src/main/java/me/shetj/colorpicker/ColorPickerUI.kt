package me.shetj.colorpicker

import com.google.android.material.slider.RangeSlider
import me.shetj.colorpicker.databinding.LayoutColorPickerBinding

/**
 * 默认初始
 */
class ColorPickerUI {

    var argb :String = "#FFFFFFFF"

    var mViewBinding:LayoutColorPickerBinding ? = null


    fun init(){
        mViewBinding?.apply {
            sliderA.addOnSliderTouchListener(object :RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {

                }

                override fun onStopTrackingTouch(slider: RangeSlider) {

                }
            })
            sliderR.addOnSliderTouchListener(object :RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {

                }

                override fun onStopTrackingTouch(slider: RangeSlider) {

                }
            })
            sliderG.addOnSliderTouchListener(object :RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {

                }

                override fun onStopTrackingTouch(slider: RangeSlider) {

                }
            })
            sliderB.addOnSliderTouchListener(object :RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {

                }

                override fun onStopTrackingTouch(slider: RangeSlider) {

                }
            })
        }
    }


}