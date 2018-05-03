package business


import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import utils.*
import java.util.concurrent.ExecutorService
import kotlin.concurrent.thread

//实例化的索引号，表示第几个实例
private var INSTACNCE_INDEX = 0
class ObserverRx(val isFlow:Boolean=true,val isLogv:Boolean=true,val isPill:Boolean=true) {
    var ringTBreak:Long = 0L
    var dump:((String)->Unit)? = null
    private val log:LogBuilder

    init {
        ++INSTACNCE_INDEX
        log = LogBuilder("${this.javaClass.simpleName}-$INSTACNCE_INDEX").apply {
            busEnd = { println("-->busEnd")}
        }
    }

    fun pill(pillTag:Any,period:Int,it:Any):String {
        log.pillingThread("t${pillTag}")
        trySleep(period.toLong())
        return "T$pillTag:$it"
    }
    fun pill1(period: Int, it:Any) = pill(1,period,it)
    fun pill2(period: Int, it:Any) = pill(2,period,it)
    fun pill3(period: Int, it:Any) = pill(3,period,it)
    fun pill4(period: Int, it:Any) = pill(4,period,it)

    /**
     * 输出信息可能被后来的覆盖
     */
    fun excuteList(list: List<Observable<out Any>>){
        list.forEach { excute(it) }
    }
    fun excute(source:Observable<out Any>) {
        updateSwitch.invoke()
        log.reset("single"){
            println("-->endAction")
            CustomScheduler.ALLCS.forEach { it.unHook() }
            //println(log.dump)
            dump?.invoke(log.dump)
        }
        source
                .doOnDispose(log::postBreak)
                .subscribe(log::preNext, log::preError, log::postComplete, log::preSubscribe)
        if (ringTBreak > 0) {
            doBreak()
        }
    }

    private val updateSwitch:()->Unit = {
        log.switch(isFlow, isLogv, isPill)
    }
    private inline fun trySleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private inline fun doBreak(){
        thread(true,true){
            trySleep(ringTBreak)
            log.disposer?.dispose()
        }
    }

}