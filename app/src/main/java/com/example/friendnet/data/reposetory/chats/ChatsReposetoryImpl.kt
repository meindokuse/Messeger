package com.example.friendnet.data.reposetory.chats

import android.util.Log
import com.example.friendnet.data.models.remote.ChatDto
import com.example.friendnet.data.models.remote.ListForDeleteChat
import com.example.friendnet.data.models.remote.UserForChooseDto
import com.example.friendnet.data.remote.FirebaseStorage
import com.example.friendnet.domain.reposetory.chats.ChatRepository
import com.example.friendnet.models.UserForChoose
import com.example.friendnet.util.api.DataForCreateChat
import com.example.friendnet.util.api.NetworkApi

class ChatsReposetoryImpl(
    private val userApi: NetworkApi,
    private val firebaseStorage: FirebaseStorage,
) : ChatRepository {
    override suspend fun getAllChats(userId: String, page: Int, pageSize: Int): List<ChatDto> {

       val readyChats = userApi.getAllChats(userId, page, pageSize).body()?.chats?.mapNotNull { chat ->
            val anotherUserId = if (userId == chat.user_id_1) chat.user_id_2 else chat.user_id_1
            Log.d("MyLog", "anotherUserId $anotherUserId avatar ${chat.avatar}")
            val uri = firebaseStorage.getData(anotherUserId, chat.avatar)
            uri?.let {
                chat.copy(avatar = it.toString())
            }
        }
        return readyChats ?: emptyList()
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

    override suspend fun getUserForCreateChat(userId: String): List<UserForChooseDto> {
        val users = userApi.getUsersForNewChat(userId).body()?.usersForChoose ?: return emptyList()
        Log.d("MyLog", "запрос на чаты $users")

        users.mapNotNull { user ->
            val uri = firebaseStorage.getData(user.id, user.foto)

            Log.d("MyLog", "$uri")

            uri?.let {
                Log.d("MyLog", "uri сработал")
                user.copy(foto = it.toString())
            }
        }
        Log.d("MyLog", "getUserForCreateChat $users")


        return users
    }

    override suspend fun deleteChats(chatsId: List<String>): Boolean {
        return userApi.deleteChat(ListForDeleteChat(chatsId)).isSuccessful
    }

}