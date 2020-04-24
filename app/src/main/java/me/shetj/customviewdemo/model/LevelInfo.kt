package me.shetj.customviewdemo.model

class LevelInfo {
    /**
     * total_learn_days : 71
     * today_learn_time : 0
     * consecutive_days : 3
     * week_rank : 0
     * total_learn_hours : 48.64
     * score : 21
     * medal_info : {"status":0,"tip":""}
     * card_surface_bubble : false
     * card_pop_window : false
     * calendar_red_point : true
     * level : 2
     * level_prompt : 0
     */
    var total_learn_days = 0
    var today_learn_time = 0
    var consecutive_days = 0
    var week_rank = 0
    var total_learn_hours = 0.0
    var score = 0
    var medal_info: MedalInfoBean? = null
    var isCard_surface_bubble = false
    var isCard_pop_window = false
    var isCalendar_red_point = false
    var level = 0
    var level_prompt = 0

    class MedalInfoBean {
        /**
         * status : 0
         * tip :
         */
        var status = 0
        var tip: String? = null

    }
}