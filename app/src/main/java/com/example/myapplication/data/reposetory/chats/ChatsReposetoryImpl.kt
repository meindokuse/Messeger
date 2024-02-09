package com.example.myapplication.data.reposetory.chats

import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.remote.RetrofitStorage
import com.example.myapplication.domain.reposetory.chats.RemoteChatsReposetory
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.UserForChoose
import com.example.myapplication.util.api.DataForCreateChat

class ChatsReposetoryImpl(
    private val firebaseStorage: FirebaseStorage,
) : RemoteChatsReposetory {
    override suspend fun getAllChats(userId: String): List<ItemChat> {
        val readyChats = ArrayList<ItemChat>()
        RetrofitStorage.getAllChats(userId)?.forEach { chat ->
            val uri = firebaseStorage.getData(userId,chat.avatar)
            uri?.let {
                readyChats.add(chat.copy(avatar = uri.toString()))
            }
        }
        return readyChats
    }

    override suspend fun createNewChat(dataForCreateChat: DataForCreateChat): ItemChat? {
        val userId = dataForCreateChat.message.id_sender
        val response = RetrofitStorage.createNewChat(dataForCreateChat)
        return if (response.isSuccessful) {
            val chat = dataForCreateChat.chat
            val uri = firebaseStorage.getData(userId,chat.avatar)
            uri?.let {
                chat.copy(avatar = uri.toString())
            }
        } else {
            null
        }
    }

    override suspend fun getUserForCreateChat(userId: String): List<UserForChoose>? {
        return RetrofitStorage.getUsersForCreateNewChat(userId)
    }

}