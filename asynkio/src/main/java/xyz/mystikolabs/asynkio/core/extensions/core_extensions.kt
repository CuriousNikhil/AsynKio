package xyz.mystikolabs.asynkio.core.extensions

class AsyncException(e: Exception, stackTrace: Array<out StackTraceElement>) : RuntimeException(e) {
    init {
        this.stackTrace = stackTrace
    }
}