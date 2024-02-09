package com.example.myapplication.domain.interactor

import com.example.myapplication.domain.reposetory.chats.ChatLocalReposetory
import com.example.myapplication.domain.reposetory.chats.RemoteChatsReposetory
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.UserForChoose
import com.example.myapplication.util.api.DataForCreateChat

class ChatCases(
    private val chatLocalReposetory: ChatLocalReposetory,
    private val remoteChatsReposetory: RemoteChatsReposetory
) {

    suspend fun addNewChat(dataForCreateChat: DataForCreateChat): ItemChat? {
        return remoteChatsReposetory.createNewChat(dataForCreateChat)
    }

    suspend fun getChats(userId: String): List<ItemChat>? {
        return remoteChatsReposetory.getAllChats(userId)
    }

    suspend fun getUsersForNewChat(userId: String): List<UserForChoose>? {
        return remoteChatsReposetory.getUserForCreateChat(userId = userId)
    }

}