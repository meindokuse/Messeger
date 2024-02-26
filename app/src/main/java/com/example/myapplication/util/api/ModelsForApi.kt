package com.example.myapplication.util.api

import com.example.myapplication.models.MessageInChat
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UserForChoose
import com.google.gson.annotations.SerializedName

data class User(
    val user: ProfileInfo
)

data class MessageResponse(
    @SerializedName("MessageResponse")
    val messages: List<MessageInChat>
)

data class ChatResponse(
    @SerializedName("ChatResponse")
    val chats:List<ItemChat>
)

data class UsersForNewChatResponse(
    @SerializedName("UsersForNewChatResponse")
    val usersForChoose: List<UserForChoose>
)

data class DataForCreateChat(
    val chat: ItemChat,
    val message: MessageInChat
)