package me.shetj.customviewdemo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.shetj.base.ktx.toJson
import org.junit.Test

typealias test1  = () -> Boolean

class KotlinTest {

    /**
     * 数组复制
     */
    @Test
    fun testCopy() {

        val arrayOf1 = arrayOf(1, 2, 3, 4, 5, 6)

        val copyInto = arrayOf1.copyInto(arrayOfNulls<Int>(6), startIndex = 0, endIndex = 3)

        println(arrayOf1.toJson())
        println(copyInto.toJson())

        TestData("1", "2").component1()

        val (t1, t2) = func("1", "2")

    }


    fun func(t: String, t2: String): TestData {
        return TestData(t, t2)
    }

    data class TestData(val t: String, val l: String) {


    }



    fun testMutex() {
        runBlocking {
            val mutex = Mutex()
            //相当于Java锁
            mutex.withLock {


            }
        }
    }

    fun CoroutineScope.produceNumbers() = produce<Int> {
        var x = 1
        while (true) send(x++) // 在流中开始从 1 生产无穷多个整数
    }

    suspend fun testChannel() {
        val chanel = Channel<Int>(4)
        GlobalScope.launch {
            repeat(10) {
                chanel.send(it)
            }
            chanel.close()
        }
        chanel.receive()
        //需要接受
    }

}