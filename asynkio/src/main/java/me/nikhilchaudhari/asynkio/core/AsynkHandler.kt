package me.nikhilchaudhari.asynkio.core

import android.app.Activity
import android.app.Fragment
import android.os.Handler
import android.os.Looper
import android.os.Message
import me.nikhilchaudhari.asynkio.extensions.AsyncException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


private val workers = WeakHashMap<Any, ExecutorService>()

class AsynkHandler(private val target: Any) {

    private var errorHandler: ErrorHandler? = null
    private var finallyHandler: (() -> Unit)? = null

    private val uiThreadHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (isAlive()) {
                @Suppress("UNCHECKED_CAST")
                (msg.obj as () -> Unit)()
            }
        }
    }

    internal var currentTask: CancelableTask<*>? = null

    private lateinit var uiThreadStackTrace: Array<out StackTraceElement>


    fun onError(errorHandler: ErrorHandler): AsynkHandler {
        this.errorHandler = errorHandler
        return this
    }


    fun finally(finallyHandler: () -> Unit) {
        this.finallyHandler = finallyHandler
    }

    internal fun cancel() {
        currentTask?.cancel()
    }

    internal fun <V> handleException(originalException: Exception, continuation: Continuation<V>) {
        runOnUi {
            currentTask = null

            try {
                continuation.resumeWithException(originalException)
            } catch (e: Exception) {
                val asyncException = AsyncException(e, refineUiThreadStackTrace())
                errorHandler?.invoke(asyncException) ?: throw asyncException
            }

            applyFinallyBlock()
        }
    }

    internal fun applyFinallyBlock() {
        if (isLastCoroutineResumeExecuted()) {
            finallyHandler?.invoke()
        }
    }

    private fun isLastCoroutineResumeExecuted() = currentTask == null

    private fun isAlive(): Boolean {
        return when (target) {
            is Activity -> return !target.isFinishing
            is Fragment -> return target.activity != null && !target.isDetached
            else -> true
        }
    }

    internal fun runOnUi(block: () -> Unit) {
        uiThreadHandler.obtainMessage(0, block).sendToTarget()
    }

     private fun holdCallerStackTrace() {
        uiThreadStackTrace = Thread.currentThread().stackTrace
    }

    private fun refineUiThreadStackTrace(): Array<out StackTraceElement> {
        return uiThreadStackTrace
            .dropWhile { it.methodName != "holdCallerStackTrace" }
            .drop(2)
            .toTypedArray()
    }


    private fun Any.getExecutorService(): ExecutorService {
        val threadName = "AsynKio-${this::class.java.simpleName}"
        return workers.getOrElse(this) {
            val newExecutor = Executors.newSingleThreadExecutor(AsyncThreadFactory(threadName))
            workers[this] = newExecutor
            newExecutor
        }
    }

    suspend fun <V> await(f: () -> V): V {
        holdCallerStackTrace()
        return suspendCoroutine {
            currentTask = AwaitTask(f, this, it)
            target.getExecutorService().submit(currentTask)
        }
    }

    suspend fun <V, P> awaitWithProgress(
        f: (ProgressHandler<P>) -> V,
        onProgress: ProgressHandler<P>
    ): V {
        holdCallerStackTrace()
        return suspendCoroutine {
            currentTask = AwaitWithProgressTask(f, onProgress, this, it)
            target.getExecutorService().submit(currentTask)
        }
    }
}