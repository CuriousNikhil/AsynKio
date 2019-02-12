
Response Contents
=================

### Status Code
You can check the response status code

    async{
        val r = await{ get("https://your.api/") }
        r.statusCode //200, 501, 404 etc
    }

### Accessing server headers
Can view server's headers. The response returns a map of headers

    r.headers
    // {Server=nginx, Access-Control-Allow-Origin=*,  Access-Control-Allow-Credentials=true, etc}

So, we can access the headers using any capitalization we want:

    headers["Content-Type"]
    // application/json
    r.headers.get("content-type")
    // application/json


### Redirection and History
We can use the `history` property of the `Response` object to track redirection.
The history list contains the Response objects that were created in order to complete the request.
The list is sorted from the oldest to the most recent response.

For example, GitHub redirects all HTTP requests to HTTPS:

    val r = get("http://github.com")
    r.url
    // https://github.com/
    r.statusCode
    // 200
    r.history
    // [<Response [301]>]

If you want to disable redirection handling, you can do so with the allowRedirects parameter.

    val r = get("http://github.com", allowRedirects = false)
    r.statusCode
    // 301
    r.history


### Timeouts
You can tell Asynkio to stop waiting for a response after a given number of seconds with the `timeout` parameter.

    async{
        val r = await{ get("https://your.api/", timeout=0.001) }
    }
    // Will throw java.net.SocketTimeoutException: connect timed out