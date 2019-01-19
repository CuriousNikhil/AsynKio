Getting Started
==============


Install the Asynkio in your project.
Add the following line as a dependency in your app level `build.gradle` file

[](_media/implementation.txt ':include :type=code groovy')


## Usage:Basics

### async
`async` is where you pass your code block as a lambda

    async{
        //suspending lambda/ coroutine
    }

### await
`await` is where you pass your long running code (It is a  suspending function)
You can use multiple `await` in your `async` block.

    async{
        await{
            //long running code or Network request
            service.getResponse()
        }
    }

You can get result of long running code as a return value from `await` and you can process this inside of the `async` block which will run the respective code on UI thread.

    async {
        val users = await { service.getUsers() }
        populateViews(users)

        users.forEach{ user ->
            status = await { service.checkUserStatus(user.id) }
            setOnlineStatusOfUser(status)
        }
    }

### Error Handling
**Handle the errors with `try/catch`**

    async {
       try {
          val result = await {
             // throw exception in background thread
          }
          // process your result
       } catch (e: Exception) {
          // Handle exception in UI thread
       }
    }