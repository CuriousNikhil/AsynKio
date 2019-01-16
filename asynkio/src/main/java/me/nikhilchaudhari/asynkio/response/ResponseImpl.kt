package me.nikhilchaudhari.asynkio.response

import org.json.JSONArray
import org.json.JSONObject
import me.nikhilchaudhari.asynkio.extensions.getSuperclasses
import me.nikhilchaudhari.asynkio.helper.CaseInsensitiveMap
import me.nikhilchaudhari.asynkio.request.Request
import me.nikhilchaudhari.asynkio.request.RequestImpl
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream


class ResponseImpl internal constructor(override val request: Request) : Response {

    internal companion object {


        internal fun HttpURLConnection.forceMethod(method: String) {
            try {
                this.requestMethod = method
            } catch (ex: ProtocolException) {
                try {
                    (this.javaClass.getDeclaredField("delegate")
                        .apply {
                            this.isAccessible = true
                        }.get(this) as HttpURLConnection?)?.forceMethod(method)
                } catch (ex: NoSuchFieldException) {
                }
                (this.javaClass.getSuperclasses() + this.javaClass).forEach {
                    try {
                        it.getDeclaredField("method").apply { this.isAccessible = true }
                            .set(this, method)
                    } catch (ex: NoSuchFieldException) {
                    }
                }
            }
            check(this.requestMethod == method)
        }

        internal val defaultStartInitializers: MutableList<(ResponseImpl, HttpURLConnection) -> Unit> =
            arrayListOf(
                { response, connection ->
                    connection.forceMethod(response.request.method)
                },
                { response, connection ->
                    for ((key, value) in response.request.headers) {
                        connection.setRequestProperty(key, value)
                    }
                },
                { response, connection ->
                    val timeout = (response.request.timeout * 1000.0).toInt()
                    connection.connectTimeout = timeout
                    connection.readTimeout = timeout
                },
                { _, connection ->
                    connection.instanceFollowRedirects = false
                }
            )

        internal val defaultEndInitializers: MutableList<(ResponseImpl, HttpURLConnection)
        -> Unit> = arrayListOf(
            { response, connection ->
                val body = response.request.body
                if (body.isEmpty()) return@arrayListOf
                connection.doOutput = true
                connection.outputStream.use { it.write(body) }
            }
        )
    }

    internal fun URL.openRedirectingConnection(
        first: Response,
        receiver: HttpURLConnection.() -> Unit
    )
            : HttpURLConnection {
        val connection = (this.openConnection() as HttpURLConnection).apply {
            this.instanceFollowRedirects = false
            this.receiver()
            this.connect()
        }
        if (first.request.allowRedirects &&
            connection.responseCode in arrayOf(301, 302, 303, 307, 308)) {
            val req = with(first.request) {
                ResponseImpl(
                    RequestImpl(
                        method = this.method,
                        url = this@openRedirectingConnection.toURI()
                            .resolve(connection.getHeaderField("Location")).toASCIIString(),
                        headers = this.headers,
                        params = this.params,
                        data = this.data,
                        auth = this.auth,
                        json = this.json,
                        timeout = this.timeout,
                        allowRedirects = false,
                        stream = this.stream
                    )
                )
            }
            req._history.addAll(first.history)
            (first as ResponseImpl)._history.add(req)
            req.init()
        }
        return connection
    }

    internal var _history: MutableList<Response> = arrayListOf()
    override val history: List<Response>
        get() = Collections.unmodifiableList(this._history)

    private var _connection: HttpURLConnection? = null
    override val connection: HttpURLConnection
        get() {
            if (this._connection == null) {
                this._connection = URL(this.request.url).openRedirectingConnection(
                    this._history.firstOrNull() ?: this.apply { this._history.add(this) }) {
                    (ResponseImpl.defaultStartInitializers + this@ResponseImpl.initializers +
                            ResponseImpl.defaultEndInitializers).forEach {
                        it(this@ResponseImpl, this)
                    }
                }
            }
            return this._connection ?: throw IllegalStateException("Set to null by another thread")
        }

    private var _statusCode: Int? = null
    override val statusCode: Int
        get() {
            if (this._statusCode == null) {
                this._statusCode = this.connection.responseCode
            }
            return this._statusCode ?: throw IllegalStateException("Set to null by another thread")
        }

    private var _headers: Map<String, String>? = null
    override val headers: Map<String, String>
        get() {
            if (this._headers == null) {
                this._headers =
                        this.connection.headerFields.mapValues { it.value.joinToString(", ") }
                            .filterKeys { it != null }
            }
            val headers =
                this._headers ?: throw IllegalStateException("Set to null by another thread")
            return CaseInsensitiveMap(headers)
        }

    private val HttpURLConnection.realInputStream: InputStream
        get() {
            val stream = try {
                this.inputStream
            } catch (ex: IOException) {
                this.errorStream
            }
            return when (this@ResponseImpl.headers["Content-Encoding"]?.toLowerCase()) {
                "gzip" -> GZIPInputStream(stream)
                "deflate" -> InflaterInputStream(stream)
                else -> stream
            }
        }

    private var _raw: InputStream? = null
    override val raw: InputStream
        get() {
            if (this._raw == null) {
                this._raw = this.connection.realInputStream
            }
            return this._raw ?: throw IllegalStateException("Set to null by another thread")
        }

    private var _content: ByteArray? = null
    override val content: ByteArray
        get() {
            if (this._content == null) {
                this._content = this.raw.use { it.readBytes() }
            }
            return this._content ?: throw IllegalStateException("Set to null by another thread")
        }

    override val text: String
        get() = this.content.toString(this.encoding)

    override val jsonObject: JSONObject
        get() = JSONObject(this.text)

    override val jsonArray: JSONArray
        get() = JSONArray(this.text)


    override val url: String
        get() = this.connection.url.toString()

    private var _encoding: Charset? = null
        set(value) {
            field = value
        }
    override var encoding: Charset
        get() {
            if (this._encoding != null) {
                return this._encoding
                    ?: throw IllegalStateException("Set to null by another thread")
            }
            this.headers["Content-Type"]?.let {
                val charset = it.split(";").map { it.split("=") }
                    .filter { it[0].trim().toLowerCase() == "charset" }.filter { it.size == 2 }
                    .map { it[1] }.firstOrNull()
                return Charset.forName(charset?.toUpperCase() ?: Charsets.UTF_8.name())
            }
            return Charsets.UTF_8
        }
        set(value) {
            this._encoding = value
        }

    // Initializers
    val initializers: MutableList<(ResponseImpl, HttpURLConnection) -> Unit> = arrayListOf()


    override fun toString(): String {
        return "<Response [${this.statusCode}]>"
    }

    private fun <T : URLConnection> Class<T>.getField(name: String, instance: T): Any? {
        (this.getSuperclasses() + this).forEach { clazz ->
            try {
                return clazz.getDeclaredField(name).apply { this.isAccessible = true }.get(instance)
                    .apply { if (this == null) throw Exception() }
            } catch (ex: Exception) {
                try {
                    val delegate =
                        clazz.getDeclaredField("delegate").apply { this.isAccessible = true }
                            .get(instance)
                    if (delegate is URLConnection) {
                        return delegate.javaClass.getField(name, delegate)
                    }
                } catch (ex: NoSuchFieldException) {
                    // ignore
                }
            }
        }
        return null
    }

    private fun updateRequestHeaders() {
        val headers = (this.request.headers as MutableMap<String, String>)
        val requests = this.connection.javaClass.getField("requests", this.connection) ?: return
        @Suppress("UNCHECKED_CAST")
        val requestsHeaders = requests.javaClass.getDeclaredMethod("getHeaders")
            .apply { this.isAccessible = true }
            .invoke(requests) as Map<String, List<String>>
        headers += requestsHeaders.filterValues { it.filterNotNull().isNotEmpty() }
            .mapValues { it.value.joinToString(", ") }
    }


    internal fun init() {
        if (this.request.stream) {
            this.connection
        } else {
            this.content
        }
        this.updateRequestHeaders()
    }

}