package com.example.friendnet.util.api

import com.example.friendnet.data.models.remote.ChatDto
import com.example.friendnet.data.models.remote.MessageDto
import com.example.friendnet.data.models.remote.ProfileDto
import com.example.friendnet.data.models.remote.UserForChooseDto
import com.example.friendnet.models.UserForChoose
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
    val usersForChoose: List<UserForChooseDto>
)

data class DataForCreateChat(
    val chat: ChatDto,
    val message: MessageDto
)