package testConcat

import business.ObserverRx
import business.SimTask
import io.reactivex.Observable
import io.reactivex.ObservableSource

import io.reactivex.schedulers.Schedulers

val gRx = ObserverRx(isLogv = false).apply {
    dump = { println(it) }
}

/**
 * 场景：一个源完毕后，接着下一个源
 * .doOnNext是有顺序的
 * map.map使用不同的线程，可以让后一个尽快开始，没必要等到前一个结束
 */
fun main(argv:Array<String>){
    tCacheToFileError()

    Thread.sleep(3000)
}

//串行，前一个源完毕，才能后一个源
fun tCacheToFileComplete() {
    var cacheCount = 0
    var cacheSource: Observable<String>?
    cacheSource = Observable.create<Int> {
        for (id in 1..10) {
            gRx.pill("it($id)",0,id)
            cacheCount++
            if (cacheCount == 5){
                it.onComplete()
                break
            }else {
                it.onNext(id)
            }
        }
        it.onComplete()
    }

            //.observeOn(Schedulers.io())
            .map { gRx.pill1(100, it) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { gRx.pill2(50, it) }
            .doOnNext { gRx.pill("do($it)",0,it) }

    val fileSource = Observable.range(101, 10)
            .map { gRx.pill3(100, it) }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .map { gRx.pill4(50, it) }

    val source = Observable.concat(cacheSource, fileSource)
            //.subscribeOn(Schedulers.computation())
    gRx.excute(source){
        //println(it)
    }

    Thread.sleep(5000)
}

//有Error，当前源结束，更何况后面的源
fun tCacheToFileError() {
    var cacheCount = 0
    var cacheSource: Observable<String>?
    cacheSource = Observable.create<Int> {
        for (id in 1..10) {
            gRx.pill("it($id)",0,id)
            cacheCount++
            if (cacheCount == 5){
                it.onError(Throwable("等于5"))
                break
            }else {
                it.onNext(id)
            }
        }
        it.onComplete()
    }

            //.observeOn(Schedulers.io())
            .map { gRx.pill1(100, it) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { gRx.pill2(50, it) }
            .doOnNext { gRx.pill("do($it)",0,it) }

    val fileSource = Observable.range(101, 10)
            .map { gRx.pill3(100, it) }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .map { gRx.pill4(50, it) }

    val source = Observable.concat(cacheSource, fileSource)
    //.subscribeOn(Schedulers.computation())
    gRx.excute(source){
        //println(it)
    }

    Thread.sleep(5000)
}
