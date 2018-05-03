import business.ObserverRx
import io.reactivex.Observable
import io.reactivex.ObservableSource

import io.reactivex.schedulers.Schedulers

val gRx = ObserverRx(isLogv = false).apply {
    dump = { println(it) }
}

fun main1(argv:Array<String>){
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

fun main(argv: Array<String>){
    tCpuToIo()
}

//98-100(5)-50(4), 300
private fun tIoToCpu(){
    val source = Observable.range(1,5)
            .flatMap {
                Observable.just(it)
                        .subscribeOn(Schedulers.io())
                        .map { gRx.pill1(100, it) }
            }.flatMap {
                Observable.just(it)
                        .subscribeOn(Schedulers.computation())
                        .map { gRx.pill2(50, it) }
            }

    gRx.excute(source)

    Thread.sleep(3000)
}
//93-100(5)-50(5), 250(8)
private fun tIoToIo(){
    val source = Observable.range(1,5)
            .flatMap {
                Observable.just(it)
                        .subscribeOn(Schedulers.io())
                        .map { gRx.pill1(100, it) }
            }.flatMap {
                Observable.just(it)
                        .subscribeOn(Schedulers.io())
                        .map { gRx.pill2(50, it) }
            }

    gRx.excute(source)

    Thread.sleep(3000)
}
//77-100(4)-50(4), 325(4)
private fun tCpuToCpu(){
    val source = Observable.range(1,5)
            .flatMap {
                Observable.just(it)
                        .subscribeOn(Schedulers.computation())
                        .map { gRx.pill1(100, it) }
            }.flatMap {
                Observable.just(it)
                        .subscribeOn(Schedulers.computation())
                        .map { gRx.pill2(50, it) }
            }

    gRx.excute(source)

    Thread.sleep(3000)
}
//72-100(4)-50(4), 330(4+4)
private fun tCpuToIo(){
    val source = Observable.range(1,5)
            .flatMap {
                Observable.just(it)
                        .subscribeOn(Schedulers.computation())
                        .map { gRx.pill1(100, it) }
            }.flatMap {
                Observable.just(it)
                        .subscribeOn(Schedulers.io())
                        .map { gRx.pill2(50, it) }
            }

    gRx.excute(source)

    Thread.sleep(3000)
}