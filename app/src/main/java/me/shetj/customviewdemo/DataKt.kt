package me.shetj.customviewdemo



fun getNextLevelData(level :Int):Int{
    return when(level +1){
        1 -> 1
        2 -> 3
        3 -> 9
        4 -> 27
        5 -> 81
        6 -> 243
        7 -> 729
        8 -> 2189
        else -> 10000
    }
}