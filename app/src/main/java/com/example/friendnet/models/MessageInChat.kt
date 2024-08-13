package com.example.friendnet.models

import java.time.Duration

data class MessageInChat(
    val message_id:String,
    val id_sender:String,
    val id_chat:String,
    val content:String,
    val time:Long,
    val type:Int,
    val duration: String
)
