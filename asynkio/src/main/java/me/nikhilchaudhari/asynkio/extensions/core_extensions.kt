package me.nikhilchaudhari.asynkio.extensions

import me.nikhilchaudhari.asynkio.helper.RawFiles
import java.io.File
import java.nio.file.Path

class AsyncException(e: Exception, stackTrace: Array<out StackTraceElement>) : RuntimeException(e) {
    init {
        this.stackTrace = stackTrace
    }
}

fun File.asFile(name: String = this.name) = RawFiles(name, this)

fun Path.asFile() = RawFiles(this)

fun Path.asFile(name: String) = RawFiles(name, this)

fun String.asFile(name: String) = RawFiles(name, this)
