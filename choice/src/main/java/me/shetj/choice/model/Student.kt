package me.shetj.choice.model


data class Student(val title: String,
                   ) {

    var isSelect:Boolean = false
        set(value) {
            field = value
            SemesterProvider.studentChange.value = (value to this)
        }

    companion object{
        fun mock(i: Int, i1: Int):MutableList<Student> {

            return mutableListOf<Student>().apply {
                repeat(7){
                    add(Student("${i}期${i1}班.学生:$it"))
                }
            }
        }
    }
}