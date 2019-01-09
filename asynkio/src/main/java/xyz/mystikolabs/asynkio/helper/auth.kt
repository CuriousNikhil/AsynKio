package xyz.mystikolabs.asynkio.helper

import android.os.Build
import android.support.annotation.RequiresApi
import java.util.*

class BaseAuth(val user:String,val password:String):Auth{
    override val header: Pair<String, String>
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val b64 = Base64.getEncoder().encode("${this.user}:${this.password}"
                .toByteArray()).toString(Charsets.UTF_8)
            return "Authorization" to "Basic $b64"
        }
}

interface Auth{
    val header:Pair<String,String>
}