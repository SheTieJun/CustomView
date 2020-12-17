package me.shetj.customviewdemo

import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.ReplaySubject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.rx3.rxFlowable
import kotlinx.coroutines.rx3.rxObservable
import me.shetj.base.tools.json.GsonKit
import me.shetj.customviewdemo.utils.DateUtils2
import org.junit.Test
import java.lang.Thread.sleep


class RxSubjectTest {
    class SubjectObserver : Observer<String> {
        override fun onSubscribe(d: Disposable?) {

        }

        override fun onNext(t: String?) {
            print("${this.hashCode()}:" + t)
        }

        override fun onError(e: Throwable?) {

        }

        override fun onComplete() {

        }
    }


    @Test
    fun testWeek() {
        println("1:" + GsonKit.objectToJson(DateUtils2.get7date()))
        println("2:" + GsonKit.objectToJson(DateUtils2.get7dateAndToday()))
        println("3:" + GsonKit.objectToJson(DateUtils2.get7dateT()))
        println("4:" + GsonKit.objectToJson(DateUtils2.get7week()))
        println("5:" + GsonKit.objectToJson(DateUtils2.sevenDate))
    }


    @ExperimentalCoroutinesApi
    @Test
    fun replay() {
        rxObservable {
            send("sss")
        }.doOnNext {

        }
        rxFlowable {
            sendBlocking("xx")
        }.blockingFirst()

        val replaySubject = ReplaySubject.create<String>().apply {
            onNext("1")
            onNext("3")
            onNext("2")
        }
        replaySubject.subscribe(SubjectObserver())
        sleep(500)
        replaySubject.subscribe(SubjectObserver())
    }
}