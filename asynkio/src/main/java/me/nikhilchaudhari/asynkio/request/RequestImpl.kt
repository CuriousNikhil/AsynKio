package me.nikhilchaudhari.asynkio.request

import me.nikhilchaudhari.asynkio.helper.RawFiles
import org.json.JSONArray
import org.json.JSONObject
import me.nikhilchaudhari.asynkio.extensions.putAllIfAbsentWithNull
import me.nikhilchaudhari.asynkio.extensions.writeAndFlush
import me.nikhilchaudhari.asynkio.helper.CaseInsensitiveMutableMap
import me.nikhilchaudhari.asynkio.helper.Parameters
import java.io.StringWriter
import java.net.IDN
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import org.json.*
import me.nikhilchaudhari.asynkio.helper.Auth
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.*
import javax.net.ssl.SSLContext


class RequestImpl internal constructor(
    override val method: String,
    url: String,
    override val params: Map<String, String>,
    headers: Map<String, String?>,
    override val auth: Auth?,
    data: Any?,
    override val json: Any?,
    override val timeout: Double,
    allowRedirects: Boolean?,
    override val stream: Boolean,
    override val files: List<RawFiles>,
    override val sslContext: SSLContext?
) : Request {

    companion object {
        val DEFAULT_HEADERS = mapOf(
            "Accept" to "*/*",
            "Accept-Encoding" to "gzip, deflate"
        )
        val DEFAULT_DATA_HEADERS = mapOf(
            "Content-Type" to "text/plain"
        )
        val DEFAULT_FORM_HEADERS = mapOf(
            "Content-Type" to "application/x-www-form-urlencoded"
        )
        val DEFAULT_UPLOAD_HEADERS = mapOf(
            "Content-Type" to "multipart/form-data; boundary=%s"
        )
        val DEFAULT_JSON_HEADERS = mapOf(
            "Content-Type" to "application/json"
        )
    }

    // Request
    override val url: String
    override val headers: Map<String, String>
    override val data: Any?
    override val allowRedirects = allowRedirects ?: (this.method != "HEAD")

    private var _body: ByteArray? = null
    override val body: ByteArray
        get() {
            if (this._body == null) {
                val requestData = this.data
                val files = this.files
                if (requestData == null && files.isEmpty()) {
                    this._body = ByteArray(0)
                    return this._body
                        ?: throw IllegalStateException("Set to null by another thread")
                }
                val data: Any? = if (requestData != null) {
                    if (requestData is Map<*, *> && requestData !is Parameters) {
                        Parameters(requestData.mapKeys { it.key.toString() }.mapValues { it.value.toString() })
                    } else {
                        requestData
                    }
                } else {
                    null
                }
                if (data != null && files.isNotEmpty()) {
                    require(data is Map<*, *>) { "data must be a Map" }
                }
                val bytes = ByteArrayOutputStream()
                if (files.isNotEmpty()) {
                    val boundary = this.headers["Content-Type"]!!.split("boundary=")[1]
                    val writer = bytes.writer()
                    if (data != null) {
                        for ((key, value) in data as Map<*, *>) {
                            writer.writeAndFlush("--$boundary\r\n")
                            val keyString = key.toString()
                            writer.writeAndFlush("Content-Disposition: form-data; name=\"$keyString\"\r\n\r\n")
                            writer.writeAndFlush(value.toString())
                            writer.writeAndFlush("\r\n")
                        }
                    }
                    files.forEach {
                        writer.writeAndFlush("--$boundary\r\n")
                        writer.writeAndFlush(
                            "Content-Disposition: form-data; name=\"${it.fieldName}\"; " +
                                    "filename=\"${it.fileName}\"\r\n\r\n")
                        bytes.write(it.contents)
                        writer.writeAndFlush("\r\n")
                    }
                    writer.writeAndFlush("--$boundary--\r\n")
                    writer.close()
                } else if (data !is File && data !is InputStream) {
                    bytes.write(data.toString().toByteArray())
                }
                this._body = bytes.toByteArray()
            }
            return this._body ?: throw IllegalStateException("Set to null by another thread")
        }


    init {
        this.url = this.makeRoute(url)
        if (URI(this.url).scheme !in setOf("http", "https")) {
            throw IllegalArgumentException("Invalid schema. Only http:// and https:// are supported.")
        }
        val json = this.json
        val mutableHeaders = CaseInsensitiveMutableMap(headers.toSortedMap())

        if (json == null) {
            this.data = data
            if (data != null && this.files.isEmpty()) {
                if (data is Map<*, *>) {
                    mutableHeaders.putAllIfAbsentWithNull(RequestImpl.DEFAULT_FORM_HEADERS)
                } else {
                    mutableHeaders.putAllIfAbsentWithNull(RequestImpl.DEFAULT_DATA_HEADERS)
                }
            }
        } else {
            this.data = this.coerceToJSON(json)
            mutableHeaders.putAllIfAbsentWithNull(RequestImpl.DEFAULT_JSON_HEADERS)
        }
        mutableHeaders.putAllIfAbsentWithNull(RequestImpl.DEFAULT_HEADERS)

        if (this.files.isNotEmpty()) {
            mutableHeaders.putAllIfAbsentWithNull(RequestImpl.DEFAULT_UPLOAD_HEADERS)
            if ("Content-Type" in mutableHeaders) {
                mutableHeaders["Content-Type"] = mutableHeaders["Content-Type"]?.format(UUID.randomUUID().toString().replace("-", ""))
            }
        }

        val auth = this.auth
        if (auth != null) {
            val header = auth.header
            mutableHeaders[header.first] = header.second
        }
        val nonNullHeaders: MutableMap<String, String> =
            mutableHeaders.filterValues { it != null }.mapValues { it.value!! }.toSortedMap()

        this.headers = CaseInsensitiveMutableMap(nonNullHeaders)
    }

    private fun coerceToJSON(any: Any): String {
        if (any is JSONObject || any is JSONArray) {
            return any.toString()
        } else if (any is Map<*, *>) {
            return JSONObject(any.mapKeys { it.key.toString() }).toString()
        } else if (any is Collection<*>) {
            return JSONArray(any).toString()
        } else if (any is Iterable<*>) {
            return any.withJSONWriter { jsonWriter, _ ->
                jsonWriter.array()
                for (thing in any) {
                    jsonWriter.value(thing)
                }
                jsonWriter.endArray()
            }
        } else if (any is Array<*>) {
            return JSONArray(any).toString()
        } else {
            throw IllegalArgumentException("Could not coerce ${any.javaClass.simpleName} to JSON.")
        }
    }

    private fun <T> T.withJSONWriter(converter: (JSONStringer, T) -> Unit): String {
        val stringWriter = StringWriter()
        val writer = JSONStringer()
        converter(writer, this)
        return stringWriter.toString()
    }

    private fun URL.toIDN(): URL {
        val newHost = IDN.toASCII(this.host)
        this.javaClass.getDeclaredField("host").apply { this.isAccessible = true }
            .set(this, newHost)
        this.javaClass.getDeclaredField("authority")
            .apply { this.isAccessible = true }
            .set(this, if (this.port == -1) this.host else "${this.host}:${this.port}")
        val query = if (this.query == null) {
            null
        } else {
            URLDecoder.decode(this.query, "UTF-8")
        }
        return URL(
            URI(
                this.protocol,
                this.userInfo,
                this.host,
                this.port,
                this.path,
                query,
                this.ref
            ).toASCIIString()
        )
    }

    private fun makeRoute(route: String) =
        URL(
            route +
                    if (this.params.isNotEmpty()) "?${Parameters(this.params)}" else ""
        ).toIDN().toString()

}
