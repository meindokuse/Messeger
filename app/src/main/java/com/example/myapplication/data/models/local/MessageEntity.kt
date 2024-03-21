package com.example.myapplication.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class MessageEntity(

    @PrimaryKey
    val messageId:String,

    val idSender:String,
    val idChat:String,
    val content:String,
    val time:Long,
    val type:Int
)
