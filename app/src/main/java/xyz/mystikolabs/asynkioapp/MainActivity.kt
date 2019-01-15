package xyz.mystikolabs.asynkioapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import xyz.mystikolabs.asynkio.core.*
import xyz.mystikolabs.asynkio.helper.BaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        async {
            val result = await {
                get("https://blog-alpha.testbook.com/mobile_blog_api.php?type=2",
                    auth = BaseAuth("Authorization","tbbloguser:testb00k"))
            }
            result_text.text = result.text
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        async.cancelAll()
    }

}
