package me.shetj.choice.model.node

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.entity.node.BaseNode
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

    val semesterList: MutableLiveData<MutableList<BaseNode>> = MutableLiveData()

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




}