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
    enum class SelectType {
        GROUP, STUDENT, NONE
    }

    private var selectType: SelectType = SelectType.GROUP

    val semesterList: MutableLiveData<MutableList<Semester>> = MutableLiveData()


    val groupSelect: MutableList<Group> = ArrayList()
    val studentSelect: MutableList<Student> = ArrayList()

    companion object {
        val groupChange: MutableLiveData<Pair<Boolean, Group>> = MutableLiveData()

        val studentChange: MutableLiveData<Pair<Boolean, Student>> = MutableLiveData()

        val dataChange:MutableLiveData<Pair<MutableList<Group>, MutableList<Student>>> = MutableLiveData()
    }


    init {
        groupChange.observeForever {
            if (it.first){
                if (!groupSelect.contains(it.second)) {
                    groupSelect.add(it.second)
                }
            }else{
                groupSelect.remove(it.second)
            }
            dataChange.postValue(groupSelect to studentSelect)
        }
        studentChange.observeForever {
            if (it.first){
                if (!studentSelect.contains(it.second)) {
                    studentSelect.add(it.second)
                }
            }else{
                studentSelect.remove(it.second)
            }
            dataChange.postValue(groupSelect to studentSelect)
        }
    }




    /**
     * 初始化
     */
    fun init(selectType: SelectType = SelectType.NONE) {
        this.selectType = selectType
        semesterList.postValue(Semester.mock())
        studentSelect.clear()
        groupSelect.clear()
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