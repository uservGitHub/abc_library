import io.reactivex.Observable

fun main(argv:Array<String>){
    val source = Observable.just("a",1,"Hello")

    source.subscribe{
        println(it)
    }
}