package me.nikhilchaudhari.asynkioapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import me.nikhilchaudhari.asynkio.core.async
import me.nikhilchaudhari.asynkio.core.get
import me.nikhilchaudhari.asynkio.core.post
import me.nikhilchaudhari.asynkio.extensions.asFile
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        async {
            val response = await { get("https://reqres.in/api/users?page=2") }
            if (response.statusCode == 200) {
//                result_text.text = response.text
            }
        }

        val outputDir = cacheDir
        val outputFile = File.createTempFile("some-file", "txt", outputDir)

        async {
            val response = await {
                post(
                    "http://mystikolabs.xyz:5000",
                    files = listOf(outputFile.asFile("file"))
                )
            }
            Toast.makeText(this@MainActivity, "some", Toast.LENGTH_LONG).show()
            result_text.text = response.text
            result_text.text = response.text

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        async.cancelAll()
    }

}
