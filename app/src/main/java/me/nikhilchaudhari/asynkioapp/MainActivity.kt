package me.nikhilchaudhari.asynkioapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import me.nikhilchaudhari.asynkio.core.async
import me.nikhilchaudhari.asynkio.core.get
import me.nikhilchaudhari.asynkio.core.post
import me.nikhilchaudhari.asynkio.helper.BaseAuth
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = mapOf("this" to "this")
        async {
            val result = await { get("https://your.api", json = data, allowRedirects = false, timeout = 0.01) }

            result.history
        }
        get("some", data = JSONObject(data))


        async {
            val response = await {
                val one = get("https://isthisawesome.com/library", auth = BaseAuth("Authorization","awesome:me"), params = mapOf("library" to "Asynkio"))
                post("https://youareonfire.com/library", data = mapOf("id" to one.jsonObject["id"]))
            }
            if (response.statusCode == 200){
                result_text.text = response.text
            }


        }


        async {
            val observable = Observable.just("Belllooo")
            val result = await { observable }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        async.cancelAll()
    }

}
