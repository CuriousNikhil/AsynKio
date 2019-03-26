
Using Asynkio with LiveData & Rx
================================

You can use **Asnkio** with `LiveData` by creating any extension function to `AsynkHandler` class.
And similar extension for the `Observable` of Rx.

Create extensions like this

[](_media/extensions.kt ':include :type=code kotlin')

and you can use this extensions in an `activity/ Fragment` etc as

    async {
        val observable = Observable.just("O")
        val result = await(observable)
    }

    val mutableLivedata = MutableLiveData<String>()
    async {
        mutableLivedata.postValue("SomeString")
        val result  = await(mutableLivedata)
    }
