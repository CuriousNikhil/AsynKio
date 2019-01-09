package xyz.mystikolabs.asynkio.core

import android.app.Activity
import android.app.Fragment
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.*

private val coroutines = WeakHashMap<Any, ArrayList<WeakReference<AsynkHandler>>>()

fun Any.async(c: suspend AsynkHandler.() -> Unit): AsynkHandler {
    val controller = AsynkHandler(this)
    keepCoroutineForCancelPurpose(controller)
    return async(c, controller)
}


fun Activity.async(c: suspend AsynkHandler.() -> Unit): AsynkHandler {
    val controller = AsynkHandler(this)
    keepCoroutineForCancelPurpose(controller)
    return async(c, controller)
}


fun Fragment.async(c: suspend AsynkHandler.() -> Unit): AsynkHandler {
    val controller = AsynkHandler(this)
    keepCoroutineForCancelPurpose(controller)
    return async(c, controller)
}

//fun android.support.v4.app.Fragment.async(c: suspend AsynkHandler.() -> Unit): AsynkHandler {
//    val controller = AsynkHandler(this)
//    keepCoroutineForCancelPurpose(controller)
//    return async(c, controller)
//}

internal fun async(c: suspend AsynkHandler.() -> Unit,
                   controller: AsynkHandler
): AsynkHandler {
    c.startCoroutine(controller, completion = object : Continuation<Unit> {
        override fun resumeWith(result: Result<Unit>) {
        }
        override val context: CoroutineContext = EmptyCoroutineContext

    })
    return controller
}

typealias ErrorHandler = (Exception) -> Unit

typealias ProgressHandler<P> = (P) -> Unit

private fun Any.keepCoroutineForCancelPurpose(controller: AsynkHandler) {
    val list = coroutines.getOrElse(this) {
        val newList = ArrayList<WeakReference<AsynkHandler>>()
        coroutines[this] = newList
        newList
    }

    list.add(WeakReference(controller))
}

class AsyncThreadFactory(val name: String) : ThreadFactory {
    private var counter = 0
    override fun newThread(r: Runnable): Thread {
        counter++
        return Thread(r, "$name-$counter")
    }
}

val Any.async: Async
    get() = Async(this)

class Async(private val asyncTarget: Any) {
    fun cancelAll() {
        coroutines[asyncTarget]?.forEach {
            it.get()?.cancel()
        }
    }
}

internal abstract class CancelableTask<V>(@Volatile
                                          var asynkHandler: AsynkHandler?,
                                          @Volatile
                                          var continuation: Continuation<V>?) : Runnable {

    private val isCancelled = AtomicBoolean(false)

    internal open fun cancel() {
        isCancelled.set(true)
        asynkHandler = null
        continuation = null
    }

    override fun run() {
        if (isCancelled.get()) return

        try {
            val value = obtainValue()
            if (isCancelled.get()) return
            asynkHandler?.apply {
                runOnUi {
                    currentTask = null
                    continuation?.resume(value)
                    applyFinallyBlock()
                }
            }

        } catch (e: Exception) {
            if (isCancelled.get()) return

            continuation?.apply {
                asynkHandler?.handleException(e, this)
            }
        }
    }

    abstract fun obtainValue(): V
}

