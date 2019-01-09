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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        async {
            Log.i("ala","ala")
            val obj = JSONObject()
            obj.put("email","nikhil@nikhil.com")
            obj.put("password","bhokat")
            val result = await {
                post(url="https://reqres.in/api/register",data = obj)
            }
            if (result.statusCode == 201){
                Log.i("ala","ajun khali ala")
                result_text.text = result.jsonObject.toString()
                Toast.makeText(this@MainActivity,"${result.jsonObject}",Toast.LENGTH_LONG).show()
            }else{
                result_text.text = result.url.plus("  ${result.statusCode}")
            }
        }

    }

}
