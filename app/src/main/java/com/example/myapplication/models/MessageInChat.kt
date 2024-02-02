package com.example.myapplication.models

data class MessageInChat(
    val message_id:String,
    val id_sender:String,
    val id_chat:String,
    val text:String,
    val time:Long
)
