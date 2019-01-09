package xyz.mystikolabs.asynkio.request

import xyz.mystikolabs.asynkio.helper.Auth

interface Request {

    val method: String

    val url: String

    val params: Map<String, String>

    val headers: Map<String, String>

    val auth:Auth?

    val data: Any?

    val json: Any?

    val timeout: Double

    val allowRedirects: Boolean

    val stream: Boolean
}