package com.example.myapplication.data.models.remote

data class MessageDto(
    val message_id:String,
    val id_sender:String,
    val id_chat:String,
    val content:String,
    val time:Long,
    val type:Int
)
