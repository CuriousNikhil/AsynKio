package xyz.mystikolabs.asynkioapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import xyz.mystikolabs.asynkio.core.async
import xyz.mystikolabs.asynkio.core.get
import xyz.mystikolabs.asynkio.core.post
import xyz.mystikolabs.asynkio.extensions.combine

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        async {
            Log.i("ala","ala")
            val obj = JSONObject()
            obj.put("email","sydney@fife")
            obj.put("password","pistol")
            val result = await {
                val users = get("https://reqres.in/api/users")
                val token = post(url="https://reqres.in/api/login",data =
                mapOf("email" to "peter@klaven", "password" to "cityslicka"))
                return@await users.combine(token)
            }
                Log.i("ala","ajun khali ala")
                result_text.text = result["0"]?.text.plus("\n").plus(result["1"]?.text)
        }

    }

}
