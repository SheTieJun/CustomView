package me.shetj.customviewdemo

import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.ReplaySubject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.rx3.rxFlowable
import kotlinx.coroutines.rx3.rxObservable
import org.junit.Test
import java.lang.Thread.sleep


class RxSubjectTest {
    class SubjectObserver:Observer<String>{
        override fun onSubscribe(d: Disposable?) {

        }

        override fun onNext(t: String?) {
          print("${this.hashCode()}:"+t)
        }

        override fun onError(e: Throwable?) {

        }

        override fun onComplete() {

        }
    }





    @ExperimentalCoroutinesApi
    @Test
    fun replay(){
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