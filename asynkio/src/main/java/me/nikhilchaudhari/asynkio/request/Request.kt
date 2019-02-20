package me.nikhilchaudhari.asynkio.request

import me.nikhilchaudhari.asynkio.extensions.RawFiles
import me.nikhilchaudhari.asynkio.helper.Auth

interface Request {

    val method: String

    val url: String

    val params: Map<String, String>

    val headers: Map<String, String>

    val auth:Auth?

    val body:ByteArray

    val data: Any?

    val json: Any?

    val timeout: Double

    val allowRedirects: Boolean

    val stream: Boolean

    val files: List<RawFiles>

}