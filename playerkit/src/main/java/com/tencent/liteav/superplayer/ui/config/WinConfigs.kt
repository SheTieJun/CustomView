package com.tencent.liteav.superplayer.ui.config


class WinConfigs {
    var showTV: Boolean = true
    var showTop: Boolean = true
    var showBottom: Boolean = true
    var showSpeed: Boolean = true

    companion object {
        fun ofDef(): WinConfigs {
            return WinConfigs()
        }

        fun ofLive(): WinConfigs {
            return WinConfigs().apply {
                showSpeed = false
                showBottom = false
                showTV = false
                showTop = false
            }
        }



        fun ofVOD(): WinConfigs {
            return WinConfigs().apply {
                showSpeed = true
                showBottom = true
                showTV = true
                showTop = true
            }
        }
    }
}