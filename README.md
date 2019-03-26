# Asynkio : Write asynced IO/ Network calls painlessly on android

[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)|
[ ![Download](https://api.bintray.com/packages/curiousnikhil/Asynkio/me.nikhilchaudhari.asynkio/images/download.svg?version=1.0.2) ](https://bintray.com/curiousnikhil/Asynkio/me.nikhilchaudhari.asynkio/1.0.2/link)|
[ ![Build Status](https://travis-ci.org/CuriousNikhil/AsynKio.svg?branch=master)](https://travis-ci.org/CuriousNikhil/AsynKio) |
[Documentation](https://curiousnikhil.github.io/AsynKio/#/)

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

Want to use it? Checkout full documentation over here [**Getting Started**](https://curiousnikhil.github.io/AsynKio/#/)


### Implementation
**`Gradle`**

    implementation 'me.nikhilchaudhari:asynkio:{latest-version}'

**`Maven`**

    <dependency>
    	<groupId>me.nikhilchaudhari</groupId>
    	<artifactId>asynkio</artifactId>
    	<version>{latest-version}</version>
    	<type>pom</type>
    </dependency>

Please check releases for latest version
