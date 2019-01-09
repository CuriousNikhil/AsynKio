package xyz.mystikolabs.asynkio.core

import kotlin.coroutines.Continuation

internal class AwaitWithProgressTask<P, V>(val f: (ProgressHandler<P>) -> V,
                                           @Volatile var onProgress: ProgressHandler<P>?,
                                           asynkHandler: AsynkHandler,
                                           continuation: Continuation<V>
)
    : CancelableTask<V>(asynkHandler, continuation) {

    override fun obtainValue(): V {
        return f { progressValue ->
            onProgress?.apply {
                asynkHandler?.runOnUi { this(progressValue) }
            }
        }
    }

    override fun cancel() {
        super.cancel()
        onProgress = null
    }

}

internal class AwaitTask<V>(val f: () -> V, asyncController: AsynkHandler, continuation: Continuation<V>)
    : CancelableTask<V>(asyncController, continuation) {
    override fun obtainValue(): V {
        return f()
    }
}