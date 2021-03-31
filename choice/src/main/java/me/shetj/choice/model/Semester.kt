package me.shetj.choice.model



data class Semester(val list: MutableList<Group>, val title: String) {

    var isExpand = false
        set(value) {
            if (!value) {
                list.forEach {
                    it.isExpand = value
                }
            }
            field = value
        }

    //是否选择全部
    var isSelect :Boolean = false



    companion object{
        fun mock():MutableList<Semester> {

            return mutableListOf<Semester>().apply {
                repeat(5){
                    add(Semester(Group.mock(),"$it:学期"))
                }
            }
        }
    }
}