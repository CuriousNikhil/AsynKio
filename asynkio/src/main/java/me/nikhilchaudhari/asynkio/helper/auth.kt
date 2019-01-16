package me.nikhilchaudhari.asynkio.helper

import java.io.ByteArrayOutputStream


fun String.encodeBase64ToString(): String = String(this.toByteArray().encodeBase64())
fun String.encodeBase64ToByteArray(): ByteArray = this.toByteArray().encodeBase64()
fun ByteArray.encodeBase64ToString(): String = String(this.encodeBase64())

fun ByteArray.encodeBase64(): ByteArray {
    val table = (CharRange('A', 'Z') + CharRange('a', 'z') + CharRange('0', '9') + '+' + '/').toCharArray()
    val output = ByteArrayOutputStream()
    var padding = 0
    var position = 0
    while (position < this.size) {
        var b = this[position].toInt() and 0xFF shl 16 and 0xFFFFFF
        if (position + 1 < this.size) b = b or (this[position + 1].toInt() and 0xFF shl 8) else padding++
        if (position + 2 < this.size) b = b or (this[position + 2].toInt() and 0xFF) else padding++
        for (i in 0 until 4 - padding) {
            val c = b and 0xFC0000 shr 18
            output.write(table[c].toInt())
            b = b shl 6
        }
        position += 3
    }
    for (i in 0 until padding) {
        output.write('='.toInt())
    }
    return output.toByteArray()
}

class BaseAuth(val user:String,val password:String):Auth{
    override val header: Pair<String, String>
        get() {
            val b64 = "${this.user}:${this.password}".encodeBase64ToString()
            return "Authorization" to "Basic $b64"
        }
}

interface Auth{
    val header:Pair<String,String>
}