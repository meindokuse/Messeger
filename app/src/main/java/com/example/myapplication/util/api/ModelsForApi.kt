package com.example.myapplication.api

import com.example.myapplication.models.MessageInChat
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.ProfileInfo

data class User(
    val user: ProfileInfo
)

data class MessageResponse(
    val messages: List<MessageInChat>
)

data class ChatResponse(
    val chats:List<ItemChat>
)

data class Chat(
    val chat_id: String,
    val user_id_1: String,
    val user_id_2: String,
    val mes_text: String,
    val mes_time: Int,
)

data class DataForCreateChat(
    val chat: ItemChat,
    val message: MessageInChat
)