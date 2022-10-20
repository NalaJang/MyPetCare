package com.example.mypetcare.database.dto

data class ChatModel(
    var users: HashMap<String, Boolean> = HashMap(),
    var comments: HashMap<String, Comment> = HashMap()) {

    class Comment(
        var uid: String? = null,
        var userName: String? = null,
        var managerName: String? = null,
        var message: String? = null,
        var time: String? = null
    )
}
