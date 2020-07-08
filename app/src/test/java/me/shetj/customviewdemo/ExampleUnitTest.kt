package me.shetj.customviewdemo

import me.shetj.base.tools.json.GsonKit.objectToJson
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun generate()  {
        val numRows = 5
        val list: MutableList<List<Int>> =
            ArrayList()
        for (i in 0 until numRows) {
            val integerList: MutableList<Int> = ArrayList(numRows)

            for (j in 0..i) {
                if (i > 1 && j > 0 && i != j) {
                    // f(i,j)=f(i−1,j−1)+f(i−1,j)
                    integerList.add(list[i - 1][j - 1] + list[i - 1][j])
                } else {
                    //其余的放1
                    integerList.add(1)
                }
            }
            println(objectToJson(integerList))
            list.add(integerList)
        }
        println(objectToJson(list))

    }
}
