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