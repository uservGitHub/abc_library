package testZip

import business.ObserverRx
import business.SimTask
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction

import io.reactivex.schedulers.Schedulers

val gRx = ObserverRx(isLogv = false).apply {
    dump = { println(it) }
}

/**
 * 场景：获取简要信息（1个），再获取详细信息（多个）
 * flatMap内的源本能否并行由源决定，
 * flatMap.subscribeOn(io)只能尽早开始，不需等待线程
 */
fun main(argv:Array<String>){
    tsAandBs()
    Thread.sleep(3000)
}

//源并行,由各自源决定
private fun tAandB(){
    val sourceA = Observable.just("a")
            .subscribeOn(Schedulers.io())
            .map { gRx.pill1(300, it) }
    val sourceB = Observable.just("b")
            .subscribeOn(Schedulers.computation())
            .map { gRx.pill2(200, it) }

    val source = Observable.zip(sourceA, sourceB, object :BiFunction<String,String,String>{
        override fun apply(t1: String, t2: String): String {
            return "$t1 + $t2"
        }
    })

    gRx.excute(source)

    Thread.sleep(3000)
}

//源并行,由各自源决定
private fun tsAandBs(){
    val sourceA = Observable.just("a","1")
            .subscribeOn(Schedulers.io())
            .map { gRx.pill1(300, it) }
    val sourceB = Observable.just("b", "2")
            .subscribeOn(Schedulers.computation())
            .map { gRx.pill2(200, it) }

    val source = Observable.zip(sourceA, sourceB, object :BiFunction<String,String,String>{
        override fun apply(t1: String, t2: String): String {
            return "$t1 + $t2"
        }
    })

    gRx.excute(source)

    Thread.sleep(3000)
}