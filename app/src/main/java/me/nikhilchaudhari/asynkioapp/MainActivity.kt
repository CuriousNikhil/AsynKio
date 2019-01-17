package me.nikhilchaudhari.asynkioapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import me.nikhilchaudhari.asynkio.core.*
import me.nikhilchaudhari.asynkio.helper.BaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        async {
            val response = await {
                val one = get("https://isthisawesome.com/library", auth = BaseAuth("Authorization","awesome:me"), params = mapOf("library" to "Asynkio"))
                val two = post("https://youareonfire.com/library", data = mapOf("id" to one.jsonObject["id"]))
                return@await two
            }
            if (response.statusCode == 200){
                result_text.text = response.text
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        async.cancelAll()
    }

}
