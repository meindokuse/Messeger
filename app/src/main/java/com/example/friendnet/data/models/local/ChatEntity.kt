package com.example.friendnet.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ChatEntity(

    @PrimaryKey
    val chatId: String,

    val userId1: String,
    val userId2: String,
    val avatar:String,
    val nickname:String,
    val mesText:String,
    val mesTime:Long
)
