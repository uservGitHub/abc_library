package business

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * 耗时任务模拟器
 */
/*
class SimTask private constructor(){

    private inline fun trySleep(millis:Long) {
        try {
            Thread.sleep(millis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}*/

object SimTask {

    fun getIds(delay: Int = 300, count: Int = 10): List<Int> {
        trySleep(delay)
        return 101.rangeTo(101 + count).toList()
    }

    fun getJieXunFromId(id: Int, delay: Int = 100): Observable<Pair<Int,String>> {
        trySleep(delay)
        return Observable.just(Pair(id, "简讯-$id"))
    }

    fun getDetailFromId(id: Int, perDelay:Int = 50): Observable<Pair<Int,String>> {
        //trySleep(perDelay)
        return Observable.fromIterable((1..3).map { Pair(id, "细节-$id--$it")})
                .doOnNext { trySleep(perDelay) }    //模拟耗时
    }
    //并行
    fun getDetailPalleFromId(id: Int, perDelay:Int = 50): Observable<Pair<Int,String>> {
        //trySleep(perDelay)
        return Observable.fromIterable((1..3).map { Pair(id, "细节-$id--$it")})
                .map { trySleep(perDelay); it }
    }



    fun getDetailDataFromId(id: Int, delay: Int = 200): List<Pair<Int,String>> {
        trySleep(delay)
        return (1..3).map { Pair(id, "细节-$id--$it") }
    }

    fun getFromCache(id: Int, delay: Int = 50): Int {
        trySleep(delay)
        return id
    }

    fun getFromFile(id: Int, delay: Int = 100): Int {
        trySleep(delay)
        return id
    }

    fun getFromNetwork(id: Int, delay: Int = 250): Int {
        trySleep(delay)
        return id
    }

    private inline fun trySleep(millis: Int) {
        try {
            Thread.sleep(millis.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

