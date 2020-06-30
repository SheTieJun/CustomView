package me.shetj.customviewdemo

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator


@SuppressLint("MissingPermission")
fun Context.vibrator( milliseconds:Long){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        (getSystemService(Service.VIBRATOR_SERVICE) as? Vibrator)?.vibrate(VibrationEffect.createOneShot(milliseconds,DEFAULT_AMPLITUDE))
    }
}