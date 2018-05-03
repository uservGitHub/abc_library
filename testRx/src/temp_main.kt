import business.ObserverRx
import io.reactivex.Observable

import io.reactivex.schedulers.Schedulers

fun main(argv:Array<String>){
    val oRx1 = ObserverRx(isLogv = false)
    //oRx1.ringTBreak = 500

    if(false){
        val sourceArray = listOf(
                Observable.just("one").map { Thread.sleep(500); "$it-sleep" },
                Observable.just("Hello", "Rx"),
                Observable.range(0,3))
        oRx1.excuteList(sourceArray)
    }else{
        val source = Observable.range(1,10)
                .subscribeOn(Schedulers.io())
                .map { oRx1.pill(1, 100, it) }
                .observeOn(Schedulers.computation())
                .map { oRx1.pill(2, 150, it) }

        oRx1.excute(source)
    }

    Thread.sleep(260*10)
}