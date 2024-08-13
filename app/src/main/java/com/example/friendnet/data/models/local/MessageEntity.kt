package com.example.friendnet.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration


@Entity
data class MessageEntity(

    @PrimaryKey
    val messageId:String,
    val idSender:String,
    val idChat:String,
    val content:String,
    val time:Long,
    val type:Int,
    val duration: String
)
