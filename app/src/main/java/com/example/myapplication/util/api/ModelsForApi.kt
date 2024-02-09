package com.example.myapplication.util.api

import com.example.myapplication.models.MessageInChat
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UserForChoose

data class User(
    val user: ProfileInfo
)

data class MessageResponse(
    val messages: List<MessageInChat>
)

data class ChatResponse(
    val chats:List<ItemChat>
)

data class UsersForNewChatResponse(
    val usersForChoose: List<UserForChoose>
)

data class DataForCreateChat(
    val chat: ItemChat,
    val message: MessageInChat
)