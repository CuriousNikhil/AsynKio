class MainActivity:AppCompatActivity(){
    //..
    //...
    override fun onCreate(savedInstanceState: Bundle?){
        //...

        async{
            val result = await{ get("https://your-website.com") }
            //process result on UI thread
            if (result.statusCode == 200){
                textView.text = result.text
            }

            // similarly you can have other methods
            //POST
            val result = await{ post("https://your-website.com/register",
                data = mapOf("name" to "john doe"))
            }

            //DELETE
            val result = await{ delete("https://...", ...) }

            //PUT
            val result = await{ put("https://...", ...) }

            // PATCH
            val result  = await{ patch("https://..." , ...) }

            //HEAD
            val result = await { head("https://your-website.com/", ...) }

            //OPTION
            val result = await{ option("https://...", ...) }
        }
    }
}
