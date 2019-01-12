package xyz.mystikolabs.asynkioapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import xyz.mystikolabs.asynkio.core.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        async {
            val userid = "2"
            val result = await {
                val user = get("https://awwapi.com/users/", params = mapOf("id" to userid))
                val cash = user.jsonObject.getInt("prize_won")

                return@await post("https://awwapi.com/reward", data = mapOf("id" to userid,
                    "reward" to cash))
            }
            println(result.text)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        async.cancelAll()
    }

}
