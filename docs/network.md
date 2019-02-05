
Handling Network Requests
=========================

### Make Requests
Here's a better way to make your requests. It's very simple in **Asynkio**.
Request methods covered and you can use are: `get`, `post`, `put`, `patch`, `delete`, `option`, `head`.

[](_media/network_request.kt ':include :type=code kotlin')

However there is one more method you can use as general method: `request`.
You can use `request(...)` to call any request-method's function.

    async{
        val result = await{
            request(method = "GET", url = "http://your-api.com", ....)
        }
       //...
       textView.text = result.jsonObject["users"]
    }


### Passing Parameters in Request
You often need to send the parameters along with url in the form  of `key=value`.
For sending the parameters one can use `map` for and pass it as a params value.

    val payload = mapOf("token" to user.token, "lang" to "en")
    async{
        val r = await{ get("https://your-api.com", params = payload) }
        println(r.url)
    }

You can check the constructed URL by printing `r.url`. It will be something like this
https://your-api.com?token=ejybaBknjasd...&lang=en