# Asynkio
**Write IO/ Network calls painlessly.**

What I mean is..

    async {
        val response = await {
            val one = get("https://isthisawesome.com/library", auth = BaseAuth("Authorization","awesome:me"), params = mapOf("library" to "Asynkio"))
            val two = post("https://youareonfire.com/library", data = mapOf("id" to one.jsonObject["id"]))
            return@await two
        }
        if (response.statusCode == 200){
            your_are_awesome.text = response.text
        }
    }

Yes, that's it. No retrofit. No Volley. Java/Kotlin are very bad at handling the http requests, but still `Asynkio` is the optimal way. Seriously...No bullshit!

Another example

    async {
        filename = await {
            longRunningFileOperation(content)
        }
    }.onError {
        Toast.makeText(context, "Oops ! it failed",Toast.LENGTH_LONG).show()
    }.finally {
        closeTheFile(filename)
    }
