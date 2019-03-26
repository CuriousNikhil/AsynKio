package me.nikhilchaudhari.asynkioapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.nikhilchaudhari.asynkio.core.async
import me.nikhilchaudhari.asynkio.core.get

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        async {
            val response = await { get("https://reqres.in/api/users?page=2") }
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
