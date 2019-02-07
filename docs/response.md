
Handling & Parsing Response Contents
====================================

We can read the content of the server’s response.

### Text response
The Text response will give us the `String` format of the response.

    async{
        val r = await { get("https://api.github.com/events") }
        result_tv.text = r.text
        // [{"repository":{"open_issues":0,"url":"https://github.com/...
    }

When you make a request, Asynkio makes educated guesses about the encoding of the response based on the HTTP headers.
The text encoding guessed by Asynkio is used when you access `r.text`. You can find out what encoding is used
and change it, using the `r.encoding` property:

        encoding_tv.text = r.encoding
        // UTF-8
        r.encoding = Charsets.ISO_8859_1


### JSON Response
In case you’re dealing with `JSON` data, Asynkio will use `org.json.json` to provide two properties: `jsonObject` and `jsonArray`.

    val r = get("https://api.github.com/events")
    println(r.jsonArray)
    // [{"actor":{"avatar_url":"https://avatars.githubusercontent.com/u/...

Note that if you attempt to access `jsonObject` but the content is an array, an exception will be thrown and vice versa.
If the content is not JSON, an exception will also be thrown.

### RAW response text
The raw response if in case you want to read for ex InputStream will be used `r.raw`. This work is in progress.
