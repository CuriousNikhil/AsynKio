
Custom Headers with No Bullshit
===============================


If you’d like to add HTTP headers to a request, simply pass in a Map to the headers parameter.

    val r = get("https://your-api.com/endpoint", headers=mapOf("X-API-Key" to "secret"))