package com.example.myapplication.chats

data class MessageInChat(
    val idMessage:String,
    val idSender:String,
    val chatId:String,
    val textOfMessage:String,
    val dataTime:Long
)
