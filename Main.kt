import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

fun main() = runBlocking{

    val num = AtomicInteger(0)
    println("Значение числа равно : $num")

    var job1: Job? = null
    var programEnded: Boolean = false

    fun startJob(): Job {
        return launch {
            while (isActive) {
                num.addAndGet(2)
                println("1-ый поток добавил к числу 2, нынешнее значение числа : $num")
                delay(1000)
            }
        }
    }

    job1 = startJob()

    val job2 = launch {
        while (isActive && !programEnded) {
            if (num.get() >= 10) {
                job1?.cancelAndJoin()
                println("1-й поток выключен")

                repeat(3) {
                    num.addAndGet(1)
                    println("2-ой поток добавил 1, нынешнее значение числа : $num")
                    delay(1000)
                }
                job1 = startJob()
                break
            }
            delay(500)
        }
        cancel()
    }

    val job3 = launch {
        while (isActive && !programEnded) {
            if (num.get() >= 30) {
                job1?.cancelAndJoin()
                num.addAndGet(10)
                println("3-ий поток добавил к числу 10, нынешнее значение числа : $num")
                programEnded = true
            }
            delay(500)
        }
        cancel()
    }

    job3.join()
    println("Программа завершила свой жизненный цикл ")
}