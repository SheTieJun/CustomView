package com.tencent.liteav.superplayer.ui.config


class FullConfigs {
    var showTV: Boolean = true
    var showShare: Boolean = true
    var showDanmu: Boolean = true
    var showTop: Boolean = true
    var showBottom: Boolean = true
    var showSpeed: Boolean = true
    var showMore: Boolean = true
    var showLock: Boolean = true
    var keepTop:Boolean = false //top 一直存在

    companion object {
        fun ofDef(): FullConfigs {
            return FullConfigs()
        }

        fun ofLive(): FullConfigs {
            return FullConfigs().apply {
                showSpeed = false
                showBottom = false
                showTV = false
                showDanmu = true
                showTop = true
                showShare = true
                showMore = false
                showLock = false
                keepTop = false
            }
        }

        fun ofVOD(): FullConfigs {
            return FullConfigs().apply {
                showSpeed = true
                showBottom = true
                showTV = true
                showDanmu = false
                showTop = true
                showShare = true
                showMore = true
                keepTop = false
            }
        }
    }
}