
Handling Multipart Data
=======================


### Sending raw files

You can use the `files` parameter to send files and multipart data with request.

    val outputDir = cacheDir
    val outputFile = File.createTempFile("some-file", "txt", outputDir)

    async {
        val response = await {
            post(
                "http://your-api.com/upload",
                files = listOf(outputFile.asFile("file"))
            )
        }
        result_text.text = response.text
    }

where `asFile(file_field_name: String)` is an extension function which allows you to
decide the name of the field which is for multipart-data in request also to set the name of the file.(Check the section below)
You can send as many files you want in one `request` henceforth the `listOf(...)` files.

### Sending raw data as file
Even further you can send a string formatted data as a file over the air in the request.

    async {
        val response = await {
            post(
                "http://your-api.com/upload",
                files = listOf("this,is,some,csv,file,".asFile("file.csv"))
            )
        }
        result_text.text = response.text
    }

The `files` parameter is a list of `RawFiles` objects.
These objects support all of the methods of uploading files available in requests, but they have a slightly different syntax to be more statically-typed.

The `asFile(name: String = ...)` extension function is available on `File`, `Path` , and `String`.
This extension function will create `RawFile` objects in a convenient manner.
You can also use the `RawFile` constructor to create a suitable object.