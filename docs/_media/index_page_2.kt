async {
    filename = await {
        longRunningFileOperation(content)
    }
}.onError {
    Toast.makeText(context, "Oops ! it failed",Toast.LENGTH_LONG).show()
}.finally {
    closeTheFile(filename)
}