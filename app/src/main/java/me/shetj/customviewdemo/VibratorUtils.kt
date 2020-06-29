package me.shetj.customviewdemo

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator


@SuppressLint("MissingPermission")
fun Context.vibarator(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        (getSystemService(Service.VIBRATOR_SERVICE) as? Vibrator)?.vibrate(VibrationEffect.createOneShot(70,10))
    }
}