package xyz.mystikolabs.asynkio.response

import org.json.JSONArray
import org.json.JSONObject
import xyz.mystikolabs.asynkio.request.Request
import java.io.InputStream
import java.net.HttpURLConnection
import java.nio.charset.Charset

interface Response {

    val request: Request

    val statusCode: Int

    val headers: Map<String, String>

    val raw: InputStream

    val content: ByteArray

    val text: String
    val jsonObject: JSONObject

    val jsonArray: JSONArray

    val url: String

    var encoding: Charset

    val history: List<Response>

    val connection: HttpURLConnection

}