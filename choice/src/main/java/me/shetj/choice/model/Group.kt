package me.shetj.choice.model


class Group(val list: MutableList<Student>, val title: String) {
    var isExpand = false
    //学生是否选择全部
    var isSelect : Boolean = false

    fun init(){

    }

    fun selectAll(isSelectAll: Boolean) {
        this.isSelect = isSelectAll
    }

    companion object{
        fun mock():MutableList<Group> {

            return mutableListOf<Group>().apply {
                repeat(5){
                    add(Group(Student.mock(),"$it:班级"))
                }
            }
        }
    }

}