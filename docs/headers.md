
Custom Headers with No Bullshit
===============================


If you’d like to add HTTP headers to a request, simply pass in a Map to the headers parameter.

    async{
        val r = await{ get("https://your.api", headers=mapOf("X-API-Key" to "secret")) }
    }
There are many times that you want to send data that is not form-encoded.
If you pass in any object except for a Map, that data will be posted directly (via the toString() method).

    val data = mapOf("this" to "this")
    async {
        val r = await { get("https://your.api", data = JSONObject(data)) }
    }

Instead of encoding the JSON yourself, you can also pass it directly using the json parameter, and it will be encoded automatically

    val data = mapOf("this" to "this")
    async {
        val r = await { get("https://your.api", json = data }
    }
