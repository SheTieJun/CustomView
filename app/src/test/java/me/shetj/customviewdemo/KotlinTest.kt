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
import java.lang.reflect.*
import java.util.*

class KotlinTest {

    interface InterVideo {
        suspend fun onHideCustomView(info: String)
    }


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


    @Test
    fun test() {
        val fields =
                arrayOf("name", "position", "salary")
        val table = "employee"
        val select = buildSelectSql(table, fields)
        println(select)
        println(if ("SELECT name, position, salary FROM employee" == select) "测试成功" else "测试失败")
    }

    fun buildSelectSql(
            table: String,
            fields: Array<String>
    ): String {
        val joiner = StringJoiner(",", "SELECT", " FROM $table")
        for (name in fields) {
            joiner.add(name)
        }
        return joiner.toString()
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

class TestProxy<T> : InvocationHandler {
    private var target //维护一个目标对象
            : T? = null

    constructor(target: T?) {
        this.target = target
    }


    override fun invoke(proxy: Any, method: Method?, args: Array<out Any>?): Any? {
        println("测试：${method?.name.toJson()}\n" +
                "genericReturnType:${method?.genericReturnType}\n" +
                "parameterAnnotations：${method?.parameterAnnotations?.size}\n" +
                "annotations：${method?.annotations?.size}\n" +
                "parameterTypes：${method?.parameterTypes?.size}\n" +
                "genericParameterTypes:${method?.genericParameterTypes?.size}\n")

        method?.parameterAnnotations?.forEach {


        }


        method?.parameterTypes?.forEach {
            println(getRawType(it)?.simpleName)


        }

        method?.genericParameterTypes?.forEach {
            println(getRawType(it)?.simpleName)

        }
        target?.let {
             method!!.invoke(target, *(args ?: arrayOfNulls<Any>(0)))
        }?: println("没有代理对象")

        /**
        测试："onHideCustomView"
        genericReturnType:class java.lang.Object
        parameterAnnotations：2
        annotations：0
        parameterTypes：2
        genericParameterTypes:2

        String
        Continuation
        String
        Continuation
         */
        return null
    }


    fun getRawType(type: Type): Class<*>? {
        Objects.requireNonNull(type, "type == null")
        if (type is Class<*>) {
            // Type is a normal class.
            return type
        }
        if (type is ParameterizedType) {

            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
            // suspects some pathological case related to nested classes exists.
            val rawType = type.rawType
            require(rawType is Class<*>)
            return rawType
        }
        if (type is GenericArrayType) {
            val componentType = type.genericComponentType
            return java.lang.reflect.Array.newInstance(getRawType(componentType), 0).javaClass
        }
        if (type is TypeVariable<*>) {
            // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
            // type that's more general than necessary is okay.
            return Any::class.java
        }
        if (type is WildcardType) {
            return getRawType(type.upperBounds[0])
        }
        throw IllegalArgumentException(
                "Expected a Class, ParameterizedType, or "
                        + "GenericArrayType, but <"
                        + type
                        + "> is of type "
                        + type.javaClass.name
        )
    }
}


/**
 * 必须是接口
 */
class ProxyFactory {
    companion object {


        //动态代理本质是静态代理是一样的，本质还是会有具体类进行实现

        inline fun <reified T> getProxy(tag: T?): T {
            val clazz = T::class.java
            val proxy = TestProxy(tag)
            return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz), proxy) as T
        }
    }
}


