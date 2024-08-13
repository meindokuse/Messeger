package com.example.friendnet.models


data class ItemChat(
    val chat_id: String,
    val user_id_1: String,
    val user_id_2: String,
    val avatar:String,
    val nickname:String,
    val mes_text:String,
    val mes_time:Long
)
