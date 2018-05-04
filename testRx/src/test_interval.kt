package testInterval

import business.ObserverRx
import business.SimTask
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction

import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

val gRx = ObserverRx(isLogv = false).apply {
    dump = { println(it) }
}

/**
 * 场景：获取简要信息（1个），再获取详细信息（多个）
 * flatMap内的源本能否并行由源决定，
 * flatMap.subscribeOn(io)只能尽早开始，不需等待线程
 */
fun main(argv:Array<String>){
    tIntervale()
    Thread.sleep(3000)
}

//源并行,由各自源决定
private fun tIntervale(){
    var acc = 0
    val source = Observable.interval(500, TimeUnit.MILLISECONDS)
            .doOnNext { ++acc }
            .map { gRx.pill("p", 0, it) }

    gRx.excute(source){ _ ->
        if (acc == 5){
            gRx.dispose()
        }
    }

    Thread.sleep(3000)
}
