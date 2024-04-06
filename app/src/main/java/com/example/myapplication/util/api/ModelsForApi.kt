package com.example.myapplication.util.api

import com.example.myapplication.data.models.local.ProfileEntity
import com.example.myapplication.data.models.remote.ChatDto
import com.example.myapplication.data.models.remote.MessageDto
import com.example.myapplication.data.models.remote.ProfileDto
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UserForChoose
import com.google.gson.annotations.SerializedName

data class User(
    val user: ProfileDto
)

data class MessageResponse(
    @SerializedName("MessageResponse")
    val messages: List<MessageDto>
)

data class ChatResponse(
    @SerializedName("ChatResponse")
    val chats:List<ChatDto>
)

data class UsersForNewChatResponse(
    @SerializedName("UsersForNewChatResponse")
    val usersForChoose: List<UserForChoose>
)

data class DataForCreateChat(
    val chat: ChatDto,
    val message: MessageDto
)