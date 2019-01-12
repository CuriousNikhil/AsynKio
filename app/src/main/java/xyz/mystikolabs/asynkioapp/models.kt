package xyz.mystikolabs.asynkioapp


data class Data(
    val data:User
)

data class User(
    val first_name:String,
    val last_name:String,
    val id:Int
)