package testFlatmap

import business.ObserverRx
import business.SimTask
import io.reactivex.Observable
import io.reactivex.ObservableSource

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
    tOneToManyPallePill()

    Thread.sleep(3000)
}

fun tOneToMany(){
    val source = SimTask.getJieXunFromId(3)
            .flatMap { SimTask.getDetailFromId(it.first) }

    gRx.excute(source)

    Thread.sleep(3000)
}
fun tOneToManyPill(){
    val source = Observable.just(2)
            .subscribeOn(Schedulers.io())
            .map { gRx.pill1(0, it) }   //44
            .flatMap { SimTask.getJieXunFromId(1) } //100
            .flatMap { SimTask.getDetailFromId(it.first) }  //50
    //第一个 44+100+50 = 200

    gRx.excute(source)

    Thread.sleep(3000)
}
//无法做到flatMap内的多个源并行，最多是立即执行内部的源
fun tOneToManyPallePill(){
    val source = Observable.just(2)
            .subscribeOn(Schedulers.io())
            .map { gRx.pill1(0, it) }   //44
            .flatMap { SimTask.getJieXunFromId(1) } //100
            .flatMap { SimTask.getDetailPalleFromId(it.first).subscribeOn(Schedulers.io()) }  //50


    gRx.excute(source)

    Thread.sleep(3000)
}
