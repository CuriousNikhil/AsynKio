package me.nikhilchaudhari.asynkio.extensions

import me.nikhilchaudhari.asynkio.helper.RawFiles
import java.io.File
import java.nio.file.Path

class AsyncException(e: Exception, stackTrace: Array<out StackTraceElement>) : RuntimeException(e) {
    init {
        this.stackTrace = stackTrace
    }
}

fun File.fileLike(name: String = this.name) = RawFiles(name, this)

fun Path.fileLike() = RawFiles(this)

fun Path.fileLike(name: String) = RawFiles(name, this)

fun String.fileLike(name: String) = RawFiles(name, this)
