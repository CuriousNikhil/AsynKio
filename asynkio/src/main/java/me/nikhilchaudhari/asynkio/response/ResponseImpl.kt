package me.nikhilchaudhari.asynkio.response

import org.json.JSONArray
import org.json.JSONObject
import me.nikhilchaudhari.asynkio.extensions.getSuperclasses
import me.nikhilchaudhari.asynkio.extensions.split
import me.nikhilchaudhari.asynkio.extensions.splitLines
import me.nikhilchaudhari.asynkio.helper.CaseInsensitiveMap
import me.nikhilchaudhari.asynkio.request.Request
import me.nikhilchaudhari.asynkio.request.RequestImpl
import java.io.File
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
import javax.net.ssl.HttpsURLConnection


class ResponseImpl internal constructor(override val request: Request) : Response {


    override fun contentIterator(chunkSize: Int): Iterator<ByteArray> {
        return object : Iterator<ByteArray> {
            var readBytes: ByteArray = ByteArray(0)
            val stream = if (this@ResponseImpl.request.stream) this@ResponseImpl.raw else this@ResponseImpl.content.inputStream()

            override fun next(): ByteArray {
                val bytes = readBytes
                val readSize = Math.min(chunkSize, bytes.size + stream.available())
                val left = if (bytes.size > readSize) {
                    return bytes.asList().subList(0, readSize).toByteArray().apply {
                        readBytes = bytes.asList().subList(readSize, bytes.size).toByteArray()
                    }
                } else if (bytes.isNotEmpty()) {
                    readSize - bytes.size
                } else {
                    readSize
                }
                val array = ByteArray(left).apply { stream.read(this) }
                readBytes = ByteArray(0)
                return bytes + array
            }

            override fun hasNext(): Boolean {
                return try {
                    val mark = this@ResponseImpl.raw.markSupported()
                    if (mark) {
                        this@ResponseImpl.raw.mark(1)
                    }
                    val read = this@ResponseImpl.raw.read()
                    if (read == -1) {
                        stream.close()
                        false
                    } else {
                        if (mark) {
                            this@ResponseImpl.raw.reset()
                        } else {
                            readBytes += ByteArray(1).apply { this[0] = read.toByte() }
                        }
                        true
                    }
                } catch(ex: IOException) {
                    false
                }
            }
        }
    }

    override fun lineIterator(chunkSize: Int, delimiter: ByteArray?): Iterator<ByteArray> {
        return object : Iterator<ByteArray> {
            val byteArrays = this@ResponseImpl.contentIterator(chunkSize)
            var leftOver: ByteArray? = null
            val overflow = arrayListOf<ByteArray>()

            override fun next(): ByteArray {
                if (overflow.isNotEmpty()) return overflow.removeAt(0)
                while (byteArrays.hasNext()) {
                    do {
                        val left = leftOver
                        val array = byteArrays.next()
                        if (array.isEmpty()) break
                        val content = if (left != null) left + array else array
                        leftOver = content
                        val split = if (delimiter == null) content.splitLines() else content.split(delimiter)
                        if (split.size >= 2) {
                            leftOver = split.last()
                            overflow.addAll(split.subList(1, split.size - 1))
                            return split[0]
                        }
                    } while (split.size < 2)
                }
                return leftOver!!
            }

            override fun hasNext() = overflow.isNotEmpty() || byteArrays.hasNext()

        }
    }

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
                { response, connection ->
                    response.request.sslContext?.let {
                        if (connection is HttpsURLConnection) {
                            connection.sslSocketFactory = it.socketFactory
                        }
                    }
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
            },
            { response, connection ->
                val files = response.request.files
                val data = response.request.data
                if (files.isNotEmpty()) return@arrayListOf
                val input = (data as? File)?.inputStream() ?: data as? InputStream ?: return@arrayListOf
                if (!connection.doOutput) {
                    connection.doOutput = true
                }
                input.use { input ->
                    connection.outputStream.use { output ->
                        while (input.available() > 0) {
                            output.write(
                                ByteArray(Math.min(4096, input.available())).apply { input.read(this) }
                            )
                        }
                    }
                }
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
                        stream = this.stream,
                        files = this.files,
                        sslContext = this.sslContext
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