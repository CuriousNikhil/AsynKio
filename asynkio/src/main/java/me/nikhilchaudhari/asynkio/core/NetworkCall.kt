@file:JvmName("NetworkCall")

package me.nikhilchaudhari.asynkio.core

import me.nikhilchaudhari.asynkio.helper.RawFiles
import me.nikhilchaudhari.asynkio.helper.Auth
import me.nikhilchaudhari.asynkio.request.RequestImpl
import me.nikhilchaudhari.asynkio.response.Response
import me.nikhilchaudhari.asynkio.response.ResponseImpl
import javax.net.ssl.SSLContext

const val DEFAULT_TIMEOUT = 30.0

@JvmOverloads
fun delete(
    url: String, headers: Map<String, String?> = mapOf(),
    params: Map<String, String> = mapOf(), auth: Auth? = null, data: Any? = null, json: Any? = null,
    timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
    stream: Boolean = false, files: List<RawFiles> = listOf(), sslContext: SSLContext? = null
): Response {
    return request(
        "DELETE",
        url,
        headers,
        params,
        auth,
        data,
        json,
        timeout,
        allowRedirects,
        stream,
        files,
        sslContext
    )
}

@JvmOverloads
fun get(
    url: String, headers: Map<String, String?> = mapOf(),
    params: Map<String, String> = mapOf(), auth: Auth? = null, data: Any? = null, json: Any? = null,
    timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
    stream: Boolean = false, files: List<RawFiles> = listOf(), sslContext: SSLContext? = null
): Response {
    return request("GET", url, headers, params, auth, data, json, timeout, allowRedirects, stream, files, sslContext)
}

@JvmOverloads
fun head(
    url: String, headers: Map<String, String?> = mapOf(),
    params: Map<String, String> = mapOf(), auth: Auth? = null, data: Any? = null,
    json: Any? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
    stream: Boolean = false, files: List<RawFiles> = listOf(), sslContext: SSLContext? = null
): Response {
    return request("HEAD", url, headers, params, auth, data, json, timeout, allowRedirects, stream, files, sslContext)
}

@JvmOverloads
fun options(
    url: String, headers: Map<String, String?> = mapOf(),
    params: Map<String, String> = mapOf(), auth: Auth? = null, data: Any? = null, json: Any? = null,
    timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
    stream: Boolean = false, files: List<RawFiles> = listOf(), sslContext: SSLContext? = null
): Response {
    return request(
        "OPTIONS",
        url,
        headers,
        params,
        auth,
        data,
        json,
        timeout,
        allowRedirects,
        stream,
        files,
        sslContext
    )
}

@JvmOverloads
fun patch(
    url: String, headers: Map<String, String?> = mapOf(),
    params: Map<String, String> = mapOf(), auth: Auth? = null, data: Any? = null, json: Any? = null,
    timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
    stream: Boolean = false, files: List<RawFiles> = listOf(), sslContext: SSLContext? = null
): Response {
    return request("PATCH", url, headers, params, auth, data, json, timeout, allowRedirects, stream, files, sslContext)
}

@JvmOverloads
fun post(
    url: String, headers: Map<String, String?> = mapOf(),
    params: Map<String, String> = mapOf(), auth: Auth? = null, data: Any? = null, json: Any? = null,
    timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
    stream: Boolean = false, files: List<RawFiles> = listOf(), sslContext: SSLContext? = null
): Response {
    return request("POST", url, headers, params, auth, data, json, timeout, allowRedirects, stream, files, sslContext)
}

@JvmOverloads
fun put(
    url: String, headers: Map<String, String?> = mapOf(),
    params: Map<String, String> = mapOf(), auth: Auth? = null, data: Any? = null, json: Any? = null,
    timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
    stream: Boolean = false, files: List<RawFiles> = listOf(), sslContext: SSLContext? = null
): Response {
    return request("PUT", url, headers, params, auth, data, json, timeout, allowRedirects, stream, files, sslContext)
}

@JvmOverloads
fun request(
    method: String, url: String, headers: Map<String, String?> = mapOf(),
    params: Map<String, String> = mapOf(), auth: Auth? = null, data: Any? = null,
    json: Any? = null, timeout: Double = DEFAULT_TIMEOUT,
    allowRedirects: Boolean? = null, stream: Boolean = false,
    files: List<RawFiles> = listOf(), sslContext: SSLContext? = null
): Response {
    return ResponseImpl(
        RequestImpl(
            method, url, params, headers, auth, data,
            json, timeout, allowRedirects, stream, files, sslContext
        )
    ).run {
        this.init()
        this._history.last().apply {
            this@run._history.remove(this)
        }
    }
}