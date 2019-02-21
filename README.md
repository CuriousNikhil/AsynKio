# Asynkio : Write asynced IO/ Network calls painlessly on android

[ ![Download](https://api.bintray.com/packages/curiousnikhil/Asynkio/me.nikhilchaudhari.asynkio/images/download.svg?version=1.0.0) ](https://bintray.com/curiousnikhil/Asynkio/me.nikhilchaudhari.asynkio/1.0.0/link)|
[ ![Build Status](https://travis-ci.org/CuriousNikhil/AsynKio.svg?branch=master)](https://travis-ci.org/CuriousNikhil/AsynKio)

Write your network requests, IO calls in android with Kotlin seamlessly.
**Asynkio** Inspired by python's [`asyncio`](https://docs.python.org/3/library/asyncio.html)

What I mean is..

    async {
        //All network requests on couroutines
        val response = await {
            //Get the data
            val resp = get("https://awesome.com/lib", params = mapOf("lib" to "Asynkio"))

            //Post the data
            return@await post("https://youareonfire.com/lib", data = mapOf("id" to resp.jsonObject["id"]))
        }
        //Process the result on UI thread
        if (response.statusCode == 200){
            yoTextView.text = response.text
        }
    }

Yes, that's it. No livedata.md. No Volley. Java/Kotlin are very bad at handling the http requests, but still `Asynkio` is the optimal way. No extra overhead, Seriously...No bullshit!

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


### Implementation
**`Gradle`**

    implementation 'me.nikhilchaudhari:asynkio:1.0.0-alpha'

**`Maven`**

    <dependency>
    	<groupId>me.nikhilchaudhari</groupId>
    	<artifactId>asynkio</artifactId>
    	<version>1.0.0-alpha</version>
    	<type>pom</type>
    </dependency>
