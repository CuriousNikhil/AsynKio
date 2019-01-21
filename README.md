# Asynkio
Write IO/ Network calls painlessly

**Write IO/ Network calls painlessly.**

What I mean is..

    async {
        //All network requests on couroutines
        val response = await {
            //Get the data
            val firstResponse = get("https://isthisawesome.com/library", params = mapOf("library" to "Asynkio"))

            //Post the data
            return@await post("https://youareonfire.com/library", data = mapOf("id" to firstResponse.jsonObject["id"]))
        }
        //Process the result on UI thread
        if (response.statusCode == 200){
            yoTextView.text = response.text
        }
    }

Yes, that's it. No retrofit. No Volley. Java/Kotlin are very bad at handling the http requests, but still `Asynkio` is the optimal way. No extra overhead, Seriously...No bullshit!

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

Want to use it? Checkout full documentation over here [Getting Started](https://curiousnikhil.github.io/AsynKio/#/)

