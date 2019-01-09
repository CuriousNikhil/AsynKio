package xyz.mystikolabs.asynkio.core.request

interface Request {

    val method: String

    val url: String

    val params: Map<String, String>

    val headers: Map<String, String>

    val data: Any?

    val json: Any?

    val timeout: Double

    val allowRedirects: Boolean

    val stream: Boolean
}