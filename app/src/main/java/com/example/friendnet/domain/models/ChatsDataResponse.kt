package com.example.friendnet.domain.models

import com.example.friendnet.models.ItemChat

data class ChatsDataResponse(
    val status: Int,
    val chats: List<ItemChat>?
)
