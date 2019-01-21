Getting Started
==============


Install the Asynkio in your project.
Add the following line as a dependency in your app level `build.gradle` file

[](_media/implementation.txt ':include :type=code groovy')


## Usage:Basics

### async
`async` is where you pass your code block as a lambda. `async` is an extension to `Any`, `Activity` and `Fragment` scope.

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

**Handle exceptions in `onError` block**

Unhandled exceptions and exception delivered in `onError` wrapped by `AsyncException`

    async{
        val result = await{
            //exception is thrown on background thread
        }
        // process the result on main thread
    }.onError{
        // Handle the exception in UI thread
    }

Use `finally{}` block to called after error or finishing coroutine

    async{
        showProgress()
        val result = await{....}
    }.onError{
        //handle exception
    }.finally{
        hideProgress()
    }


### Avoid memory leaks
Long running job on coroutine, when activity/fragment/lifecycle-aware-component is destroyed, may produce memory leaks.
To avoid this always invoke `cancelAll()` on current `async` block whenever an activity/fragment is being destroyed.

    override fun onDestroy(){
        //...
        //..
        async.cancelAll()
    }

Those are the basics of library. Check [network requests](/network.md) with AsynKio