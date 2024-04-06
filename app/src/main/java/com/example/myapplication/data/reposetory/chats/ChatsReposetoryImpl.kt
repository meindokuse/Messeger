package com.example.myapplication.data.reposetory.chats

import android.util.Log
import com.example.myapplication.data.models.remote.ChatDto
import com.example.myapplication.data.models.remote.ListForDeleteChat
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.domain.reposetory.chats.ChatRepository
import com.example.myapplication.models.UserForChoose
import com.example.myapplication.util.api.DataForCreateChat
import com.example.myapplication.util.api.NetworkApi

class ChatsReposetoryImpl(
    private val userApi: NetworkApi,
    private val firebaseStorage: FirebaseStorage,
) : ChatRepository {
    override suspend fun getAllChats(userId: String, page: Int, pageSize: Int): List<ChatDto> {
        val readyChats = ArrayList<ChatDto>()

        userApi.getAllChats(userId, page, pageSize).body()?.chats?.forEach { chat ->
            val anotherUserId = if (userId == chat.user_id_1) chat.user_id_2 else chat.user_id_1
            Log.d("MyLog", "anotherUserId $anotherUserId avatar ${chat.avatar}")
            val uri = firebaseStorage.getData(anotherUserId, chat.avatar)
            uri?.let {
                readyChats.add(chat.copy(avatar = uri.toString()))
            }
        }
        return readyChats
    }

    override suspend fun createNewChat(dataForCreateChat: DataForCreateChat): ChatDto? {
        val response = userApi.postChat(dataForCreateChat)
        return if (response.isSuccessful) {
            val chat = dataForCreateChat.chat
            chat
        } else {
            null
        }
    }

    override suspend fun getUserForCreateChat(userId: String): List<UserForChoose> {
        val readyUsers = mutableListOf<UserForChoose>()
        val users = userApi.getUsersForNewChat(userId).body()?.usersForChoose ?: return emptyList()
        Log.d("MyLog", "запрос на чаты $users")

        users.forEach { user ->
            Log.d("MyLog", "getUserForCreateChat ${user.id} ${user.nickname}")
            val uri = firebaseStorage.getData(user.id, user.foto)
            Log.d("MyLog", "$uri")

            uri?.let {
                readyUsers.add(user.copy(foto = it.toString()))
            } ?: run {
                // Обработка ситуации, когда uri равно null
                readyUsers.add(user)
            }
        }
        Log.d("MyLog", "ready users - $readyUsers")
        return readyUsers
    }

    override suspend fun deleteChats(chatsId: List<String>): Boolean {
        return userApi.deleteChat(ListForDeleteChat(chatsId)).isSuccessful
    }

}