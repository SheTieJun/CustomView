package me.shetj.choice.model

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


/**
 * 选择内容提供者
 */
class SemesterProvider {

    companion object{

        fun getInstance(){

        }
    }


    enum class SelectType {
        GROUP, STUDENT, NONE
    }

    private var selectType: SelectType = SelectType.GROUP

    val semesterList: MutableLiveData<MutableList<Semester>> = MutableLiveData()

    val semesterChange: MutableLiveData<Semester> = MutableLiveData()

    /**
     * 初始化
     */
    fun init(selectType: SelectType = SelectType.NONE) {
        this.selectType = selectType
        semesterList.postValue(Semester.mock())
    }

    fun isSelectGroup(): Boolean {
        return selectType == SelectType.GROUP
    }


    fun getSelectStudentList(): Flow<MutableList<Student>> {
        return flow {
            val list = mutableListOf<Student>()
            withContext(Dispatchers.IO) {
                semesterList.value?.forEach { semester ->
                    semester.list.forEach { group ->
                        val filter = group.list.filter {
                            return@filter it.isSelect
                        }
                        list.addAll(filter)
                    }
                }
                emit(list)
            }
        }
    }

    fun getSelectGroupList(): Flow<Group> {
        return flow {
            val list = mutableListOf<Group>()
            withContext(Dispatchers.IO) {
                semesterList.value?.forEach { semester ->
                    list.addAll(semester.list.filter { group ->
                        return@filter group.isSelect
                    })
                }
            }
        }
    }


}