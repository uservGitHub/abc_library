package testDisposed

import business.ObserverRx
import io.reactivex.Observable
import io.reactivex.ObservableSource

import io.reactivex.schedulers.Schedulers

val gRx = ObserverRx(isLogv = false).apply {
    dump = { println(it) }
}

/**
 * 在flatMap中按记录取消，毫无意义，因为来源是无序的
 */
fun main(argv:Array<String>){
    Thread.sleep(3000)
}

//45-100(1)-50(0), 45+150*5 = 800
fun tMapioToMap() {
    val source = Observable.range(1, 10)
            .map { gRx.pill1(100, it) }
            .subscribeOn(Schedulers.io())
            .map { gRx.pill2(50, it) }
    gRx.excute(source, {
        if (it.toString().indexOf("5") != -1) {
            gRx.dispose()
        }
    })
}
//45-100(1)-50(1), 45+100*5+50 = 600
fun tMapioToMapcpu() {
    val source = Observable.range(1, 10)
            .map { gRx.pill1(100, it) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { gRx.pill2(50, it) }
    gRx.excute(source, {
        if (it.toString().indexOf("5") != -1) {
            gRx.dispose()
        }
    })
}
//40-100(1)-50(1), 45+100*5+50 = 600
fun tMapioToMapio() {
    val source = Observable.range(1, 10)
            .map { gRx.pill1(100, it) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { gRx.pill2(50, it) }
    gRx.excute(source, {
        if (it.toString().indexOf("5") != -1) {
            gRx.dispose()
        }
    })
}
//40-100(1)-50(1), 45+100*5+50 = 600
fun tMapcpuToMapcpu() {
    val source = Observable.range(1, 10)
            .map { gRx.pill1(100, it) }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .map { gRx.pill2(50, it) }
    gRx.excute(source, {
        if (it.toString().indexOf("5") != -1) {
            gRx.dispose()
        }
    })
}
//52-100(10)-50(1), 52+100+50*n = ...
fun tFlatioToMapcpu() {
    val source = Observable.range(1, 10)
            .flatMap { Observable.just(it)
                    .subscribeOn(Schedulers.io())
                    .map { gRx.pill1(100, it) } }
            //.subscribeOn(Schedulers.computation()) //无意义
            .observeOn(Schedulers.computation())
            .map { gRx.pill2(50, it) }
    gRx.excute(source, {
        if (it.toString().indexOf("5") != -1) {
            gRx.dispose()
        }
    })
}