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