package com.example.myapplication.chats

data class MessageInChat(
    val id:String,
    val idSender:String,
    val chatId:String,
    val textOfMessage:String,
    val dataTime:Long
)
