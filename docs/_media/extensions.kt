
import android.arch.lifecycle.LiveData
import io.reactivex.Observable
import me.nikhilchaudhari.asynkio.core.AsynkHandler

suspend fun <V> AsynkHandler.await(observable: Observable<V>): V = this.await {
    observable.blockingFirst()
}

suspend fun <V> AsynkHandler.await(liveData: LiveData<V>):V? = this.await {
    liveData.value
}