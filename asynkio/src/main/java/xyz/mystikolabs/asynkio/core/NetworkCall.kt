@file:JvmName("NetworkCall")
@file:Suppress("UNUSED_EXPRESSION")

package xyz.mystikolabs.asynkio.core

import xyz.mystikolabs.asynkio.core.request.RequestImpl
import xyz.mystikolabs.asynkio.core.response.Response
import xyz.mystikolabs.asynkio.core.response.ResponseImpl


//suspend fun <V> AsynkHandler.awaitSuccessful(call: Call<V>) : V? = this.await {
//    val response = call.execute()
//    if (response.isSuccessful) {
//        response.body()
//    } else {
//        throw RetrofitHttpError(response)
//    }
//}
//
//class RetrofitHttpError(errorResponse: Response<*>) : RuntimeException("${errorResponse.code()}")

const val DEFAULT_TIMEOUT = 30.0
class asyncNetwork{
    companion object {
        @JvmOverloads
        fun delete(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), onResponse: suspend  Response.() -> Unit = {}): Unit {
            request("DELETE", url, headers, params, onResponse)
        }

        @JvmOverloads
        fun get(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), onResponse:suspend  Response.() -> Unit = {}): Unit {
            request("GET", url, headers, params, onResponse)
        }

        @JvmOverloads
        fun head(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), onResponse:suspend  Response.() -> Unit = {}): Unit {
            request("HEAD", url, headers, params, onResponse)
        }

        @JvmOverloads
        fun options(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), onResponse: suspend Response.() -> Unit = {}): Unit {
            request("OPTIONS", url, headers, params, onResponse)
        }

        @JvmOverloads
        fun patch(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), onResponse: suspend Response.() -> Unit = {}): Unit {
            request("PATCH", url, headers, params, onResponse)
        }

        @JvmOverloads
        fun post(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), onResponse:suspend  Response.() -> Unit = {}): Unit {
            request("POST", url, headers, params, onResponse)
        }

        @JvmOverloads
        fun put(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), onResponse:suspend  Response.() -> Unit = {}): Unit {
            request("PUT", url, headers, params, onResponse)
        }

        @JvmOverloads
        fun request(method: String, url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), onResponse: suspend Response.() -> Unit = {}): Unit {
            async {
                onResponse(xyz.mystikolabs.asynkio.core.request(method, url, headers, params))
            }.onError {
                throw it
            }
        }
    }
}


@JvmOverloads
fun delete(url: String, headers: Map<String, String?> = mapOf(),
           params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null): Response {
    return request("DELETE", url, headers, params)
}

@JvmOverloads
fun get(url: String, headers: Map<String, String?> = mapOf(),
        params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null): Response {
    return request("GET", url, headers, params)
}

@JvmOverloads
fun head(url: String, headers: Map<String, String?> = mapOf(),
         params: Map<String, String> = mapOf(), data: Any? = null): Response {
    return request("HEAD", url, headers, params)
}

@JvmOverloads
fun options(url: String, headers: Map<String, String?> = mapOf(),
            params: Map<String, String> = mapOf(), data: Any? = null): Response {
    return request("OPTIONS", url, headers, params)
}

@JvmOverloads
fun patch(url: String, headers: Map<String, String?> = mapOf(),
          params: Map<String, String> = mapOf(), data: Any? = null): Response {
    return request("PATCH", url, headers, params)
}

@JvmOverloads
fun post(url: String, headers: Map<String, String?> = mapOf(),
         params: Map<String, String> = mapOf(), data: Any? = null): Response {
    return request("POST", url, headers, params)
}

@JvmOverloads
fun put(url: String, headers: Map<String, String?> = mapOf(),
        params: Map<String, String> = mapOf(), data: Any? = null): Response {
    return request("PUT", url, headers, params)
}

@JvmOverloads
fun request(method: String, url: String, headers: Map<String, String?> = mapOf(),
            params: Map<String, String> = mapOf(),data: Any? = null,
            json: Any? = null, timeout: Double = DEFAULT_TIMEOUT,
            allowRedirects: Boolean? = null, stream: Boolean = false): Response {
    return ResponseImpl(RequestImpl(method, url, params, headers, data,
        json, timeout, allowRedirects, stream)).run {
        this.init()
        this._history.last().apply {
            this@run._history.remove(this)
        }
    }
}


