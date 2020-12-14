package me.shetj.customviewdemo.recorder

fun formatSeconds(seconds: Long): String {
    var seconds = seconds
    if (seconds > 1800) {
        seconds = 1800
    }
    return (getTwoDecimalsValue(seconds / 60) + ":"
            + getTwoDecimalsValue(seconds % 60))
}


private fun getTwoDecimalsValue(value: Long): String {
    return if (value in 0..9) {
        "0$value"
    } else {
        value.toString() + ""
    }
}