package testPattern
//https://www.cnblogs.com/shitoupi/p/7168601.html

import business.ObserverRx
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

val gRx = ObserverRx(isLogv = false).apply {
    dump = { println(it) }
}

fun main(array: Array<String>){
    tMerge()

    Thread.sleep(3000)
}

private fun tInterval(){
    var pill = 0L
    val source = Observable
            .interval(500,100, TimeUnit.MILLISECONDS)
            .doOnNext { ++pill;gRx.pill("", 0, it) }    //桩函数放这里比较好
            .subscribeOn(Schedulers.io())

    gRx.excute(source){
        if (pill == 5L){
            gRx.dispose()
        }
    }
}

private fun tMerge(){
    val sourceA = Observable.fromIterable(listOf("a","b","c","d","e"))
            .map { gRx.pill1(100, it) }
            .subscribeOn(Schedulers.computation())
    val sourceB = Observable.fromIterable(listOf(1,2))
            .map { gRx.pill2(150, it) }
            .subscribeOn(Schedulers.computation())
    val source = Observable.merge(sourceA, sourceB)

    gRx.excute(source)
}
