package xyz.mystikolabs.asynkioapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import xyz.mystikolabs.asynkio.core.*
import xyz.mystikolabs.asynkio.helper.BaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        async {
            val result = await {
                val response = get("https://blog-alpha.testbook.com/mobile_blog_api.php?type=2",
                    auth = BaseAuth("Authorization","tbbloguser:testb00k"))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        async.cancelAll()
    }

}
