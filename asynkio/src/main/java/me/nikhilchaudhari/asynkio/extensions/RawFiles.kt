package me.nikhilchaudhari.asynkio.extensions

import java.io.File
import java.nio.file.Path

class RawFiles(
    val fieldName: String, val fileName: String, val contents: ByteArray
) {
    constructor(name: String, contents: String) : this(name, contents.toByteArray())

    constructor(name: String, file: File) : this(name, file.readBytes())

    constructor(name: String, path: Path) : this(name, File(path.toString()))

    constructor(file: File) : this(file.name, file)

    constructor(path: Path) : this(File(path.toString()))

    constructor(name: String, contents: ByteArray) : this(name, name, contents)
}