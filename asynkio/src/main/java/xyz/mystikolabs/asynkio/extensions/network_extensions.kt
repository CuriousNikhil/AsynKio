package xyz.mystikolabs.asynkio.extensions

import xyz.mystikolabs.asynkio.response.Response
import java.io.Writer


internal fun Writer.writeAndFlush(string: String) {
    this.write(string)
    this.flush()
}

fun Response.combine(vararg otherResponse:Response):Map<String,Response>{
    var i = 0
    val combineResponse = mutableMapOf<String, Response>()
    combineResponse["$i"] = this
    i.plus(1)
    otherResponse.forEach {
        combineResponse["$i"] = it
        i.plus(1)
    }
    return combineResponse.toMap()
}

fun ByteArray.splitLines(): List<ByteArray> {
    if (this.isEmpty()) return listOf()
    val lines = arrayListOf<ByteArray>()
    var lastSplit = 0
    var skip = 0
    for ((i, byte) in this.withIndex()) {
        if (skip > 0) {
            skip--
            continue
        }
        if (byte == '\n'.toByte()) {
            lines.add(this.sliceArray(lastSplit..i - 1))
            lastSplit = i + 1
        } else if (byte == '\r'.toByte() && i + 1 < this.size && this[i + 1] == '\n'.toByte()) {
            skip = 1
            lines.add(this.sliceArray(lastSplit..i - 1))
            lastSplit = i + 2
        } else if (byte == '\r'.toByte()) {
            lines.add(this.sliceArray(lastSplit..i - 1))
            lastSplit = i + 1
        }
    }
    lines += this.sliceArray(lastSplit..this.size - 1)
    return lines
}

fun ByteArray.split(delimiter: ByteArray): List<ByteArray> {
    val lines = arrayListOf<ByteArray>()
    var lastSplit = 0
    var skip = 0
    for (i in 0..this.size - 1) {
        if (skip > 0) {
            skip--
            continue
        }
        if (this.sliceArray(i..i + delimiter.size - 1).toList() == delimiter.toList()) {
            skip = delimiter.size
            lines += this.sliceArray(lastSplit..i - 1)
            lastSplit = i + delimiter.size
        }
    }
    lines += this.sliceArray(lastSplit..this.size - 1)
    return lines
}

internal fun <T> Class<T>.getSuperclasses(): List<Class<in T>> {
    val list = arrayListOf<Class<in T>>()
    var superclass = this.superclass
    while (superclass != null) {
        list.add(superclass)
        superclass = superclass.superclass
    }
    return list
}

fun <K, V> MutableMap<K, V>.putIfAbsentWithNull(key: K, value: V) {
    if (key !in this) {
        this[key] = value
    }
}

fun <K, V> MutableMap<K, V>.putAllIfAbsentWithNull(other: Map<K, V>) {
    for ((key, value) in other) {
        this.putIfAbsentWithNull(key, value)
    }
}