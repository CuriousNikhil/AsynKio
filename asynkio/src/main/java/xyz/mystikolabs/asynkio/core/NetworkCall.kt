@file:JvmName("NetworkCall")

package xyz.mystikolabs.asynkio.core

import xyz.mystikolabs.asynkio.helper.Auth
import xyz.mystikolabs.asynkio.request.RequestImpl
import xyz.mystikolabs.asynkio.response.Response
import xyz.mystikolabs.asynkio.response.ResponseImpl

const val DEFAULT_TIMEOUT = 30.0

@JvmOverloads
fun delete(url: String, headers: Map<String, String?> = mapOf(),
           params: Map<String, String> = mapOf(),auth: Auth? = null, data: Any? = null, json: Any? = null,
           timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
           stream: Boolean = false): Response {
    return request("DELETE", url, headers, params,auth, data, json, timeout, allowRedirects, stream)
}

@JvmOverloads
fun get(url: String, headers: Map<String, String?> = mapOf(),
        params: Map<String, String> = mapOf(),auth: Auth? = null, data: Any? = null, json: Any? = null,
            timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
        stream: Boolean = false): Response {
    return request("GET", url, headers, params, auth, data, json, timeout, allowRedirects, stream)
}

@JvmOverloads
fun head(url: String, headers: Map<String, String?> = mapOf(),
         params: Map<String, String> = mapOf(),auth: Auth? = null, data: Any? = null,
         json: Any? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
         stream: Boolean = false): Response {
    return request("HEAD", url, headers, params, auth, data, json, timeout, allowRedirects, stream)
}

@JvmOverloads
suspend fun options(url: String, headers: Map<String, String?> = mapOf(),
            params: Map<String, String> = mapOf(), auth: Auth? = null, data: Any? = null, json: Any? = null,
            timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
            stream: Boolean = false): Response {
    return request("OPTIONS", url, headers, params,auth, data, json, timeout, allowRedirects, stream)
}

@JvmOverloads
fun patch(url: String, headers: Map<String, String?> = mapOf(),
          params: Map<String, String> = mapOf(), auth: Auth? = null, data: Any? = null, json: Any? = null,
          timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
          stream: Boolean = false): Response {
    return request("PATCH", url, headers, params, auth, data, json, timeout, allowRedirects, stream)
}

@JvmOverloads
fun post(url: String, headers: Map<String, String?> = mapOf(),
         params: Map<String, String> = mapOf(),auth: Auth? = null, data: Any? = null, json: Any? = null,
         timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
         stream: Boolean = false): Response {
    return request("POST", url, headers, params,auth, data, json, timeout, allowRedirects, stream)
}

@JvmOverloads
fun put(url: String, headers: Map<String, String?> = mapOf(),
        params: Map<String, String> = mapOf(), auth: Auth? = null, data: Any? = null, json: Any? = null,
        timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null,
        stream: Boolean = false): Response {
    return request("PUT", url, headers, params, auth, data, json, timeout, allowRedirects, stream)
}

@JvmOverloads
fun request(method: String, url: String, headers: Map<String, String?> = mapOf(),
            params: Map<String, String> = mapOf(), auth:Auth?=null,data: Any? = null,
            json: Any? = null, timeout: Double = DEFAULT_TIMEOUT,
            allowRedirects: Boolean? = null, stream: Boolean = false): Response {
    return ResponseImpl(RequestImpl(method, url, params, headers,auth, data,
        json, timeout, allowRedirects, stream)).run {
        this.init()
        this._history.last().apply {
            this@run._history.remove(this)
        }
    }
}
