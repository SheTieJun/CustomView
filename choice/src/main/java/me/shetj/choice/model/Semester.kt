package me.shetj.choice.model



data class Semester(val list: MutableList<Group>, val title: String) {

    var isExpand = false

    //是否选择全部
    var isSelect :Boolean = false



    companion object{
        fun mock():MutableList<Semester> {

            return mutableListOf<Semester>().apply {
                repeat(10){
                    add(Semester(Group.mock(it),"$it:学期"))
                }
            }
        }
    }
}