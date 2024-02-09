package com.example.myapplication.domain.models

import com.example.myapplication.models.ItemChat

data class ChatsDataResponse(
    val status:Int,
    val chats: List<ItemChat>?
)
