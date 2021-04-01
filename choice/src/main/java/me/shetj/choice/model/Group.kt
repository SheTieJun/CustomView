package me.shetj.choice.model


class Group(val list: MutableList<Student>, val title: String) {
    var isExpand = false
    //学生是否选择全部
    var isSelect : Boolean = false
        set(value) {
            field = value
            SemesterProvider.groupChange.value = (value to this)
        }


    fun init(){

    }


    companion object{
        fun mock(i: Int):MutableList<Group> {

            return mutableListOf<Group>().apply {
                repeat(5){
                    add(Group(Student.mock(i,it),"$it:班级"))
                }
            }
        }
    }

}