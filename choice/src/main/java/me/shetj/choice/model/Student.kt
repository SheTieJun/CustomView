package me.shetj.choice.model


data class Student(val title: String,
                   var isSelect:Boolean = false) {
    companion object{
        fun mock():MutableList<Student> {

            return mutableListOf<Student>().apply {
                repeat(7){
                    add(Student("学生:$it"))
                }
            }
        }
    }
}