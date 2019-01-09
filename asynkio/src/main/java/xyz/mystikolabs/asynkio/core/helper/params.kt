package xyz.mystikolabs.asynkio.core.helper

import java.net.URLEncoder

class Parameters(vararg val parameters: Pair<String, String>) : Map<String, String> by mapOf(*parameters) {

    constructor(parameters: Map<String, String>) : this(*parameters.toList().toTypedArray())

    override fun toString(): String {
        if (this.isEmpty()) return ""
        return buildString {
            for ((key, value) in this@Parameters) {
                if (this.isNotEmpty()) this.append("&")
                this.append(key, "=", URLEncoder.encode(value, "UTF-8"))
            }
        }
    }

}