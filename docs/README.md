# Asynkio
**Write IO/ Network calls painlessly.**

What I mean is..

[](_media/index_page.kt ':include :type=code kotlin')

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


